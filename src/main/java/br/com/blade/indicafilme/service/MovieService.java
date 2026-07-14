package br.com.blade.indicafilme.service;

import br.com.blade.indicafilme.dto.MovieSearchCriteria;
import br.com.blade.indicafilme.exception.BadRequestException;
import br.com.blade.indicafilme.exception.NotFoundException;
import br.com.blade.indicafilme.model.Movie;
import br.com.blade.indicafilme.model.StatusFilme;
import br.com.blade.indicafilme.repository.InMemoryMovieRepository;
import br.com.blade.indicafilme.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class MovieService {

    private static final Logger log = LoggerFactory.getLogger(MovieService.class);

    private final MovieRepository movieRepository;
    private final TmdbService tmdbService;
    private final InMemoryMovieRepository fallbackRepository = new InMemoryMovieRepository();
    private final boolean localFallbackEnabled;
    private final SecureRandom random = new SecureRandom();

    @Autowired
    public MovieService(
            MovieRepository movieRepository,
            TmdbService tmdbService,
            @Value("${app.fallback.local-enabled:false}") boolean localFallbackEnabled
            ) {
        this.movieRepository = movieRepository;
        this.tmdbService = tmdbService;
        this.localFallbackEnabled = localFallbackEnabled;
    }

    public List<Movie> filterMovies(MovieSearchCriteria filtro) {
        boolean semFiltro = estaVazio(filtro.generos())
                && estaVazio(filtro.duracoes())
                && estaVazio(filtro.decadas());

        if (semFiltro) {
            throw new BadRequestException("Ao menos um filtro deve ser informado.");
        }

        List<Movie> ativos = withMongoFallback(
                () -> movieRepository.findByStatus(StatusFilme.ATIVO),
                () -> fallbackRepository.findByStatus(StatusFilme.ATIVO),
                "findByStatus"
        );

        List<Movie> resultado = ativos.stream()
                .filter(f -> estaVazio(filtro.generos()) || matchGenero(f, filtro.generos()))
                .filter(f -> estaVazio(filtro.duracoes()) || matchDuracao(f, filtro.duracoes()))
                .filter(f -> estaVazio(filtro.decadas()) || matchDecada(f, filtro.decadas()))
                .toList();

        log.info("{} filme(s) possíveis para os filtros = {}", resultado.size(), filtro);
        return resultado;
    }

    public Optional<Movie> pickRandom(List<Movie> candidatos, String excludeId) {
        if (candidatos == null || candidatos.isEmpty()) return Optional.empty();

        List<Movie> elegiveis = candidatos;

        if (excludeId != null && !excludeId.isBlank()) {
            List<Movie> semUltimo = candidatos.stream()
                    .filter(f -> !excludeId.equals(f.getId()))
                    .toList();
            if (!semUltimo.isEmpty()) {
                elegiveis = semUltimo;
            }
        }

        Movie escolhido = elegiveis.get(random.nextInt(elegiveis.size()));
        log.info("Filme sorteado: '{}'", escolhido.getTitulo());
        return Optional.of(escolhido);
    }

    public Optional<Movie> pickRandom(List<Movie> candidatos) {
        return pickRandom(candidatos, null);
    }

    public Optional<Movie> findById(String id) {
        return withMongoFallback(
                () -> movieRepository.findById(id),
                () -> fallbackRepository.findById(id),
                "findById"
        ).map(this::enriquecerComTmdb);
    }

    public List<Movie> findAllActive() {
        return withMongoFallback(
                () -> movieRepository.findByStatus(StatusFilme.ATIVO),
                () -> fallbackRepository.findByStatus(StatusFilme.ATIVO),
                "findAllActive"
        );
    }

    public Optional<Movie> findByTitle(String titulo) {
        return withMongoFallback(
                () -> movieRepository.findByTituloIgnoreCase(titulo),
                () -> fallbackRepository.findByTituloIgnoreCase(titulo),
                "findByTitle"
        );
    }

    public Movie enriquecerComTmdb(Movie filme) {
        if (filme.getNotaPublico() == null && filme.getTitulo() != null) {
            tmdbService.buscarNotaPublico(filme.getTitulo(), filme.getAnoLancamento())
                    .ifPresent(filme::setNotaPublico);
        }
        return filme;
    }

    public Movie save(Movie filme) {
        Movie salvo = movieRepository.save(filme);
        log.info("Filme salvo: '{}' (id={})", salvo.getTitulo(), salvo.getId());
        return salvo;
    }

    public Movie atualizarStatus(String id, StatusFilme status) {
        Movie filme = movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Filme não encontrado: " + id));

        filme.setStatus(status);
        return movieRepository.save(filme);
    }

    public void deleteById(String id) {
        movieRepository.deleteById(id);
    }

    private boolean matchGenero(Movie filme, List<String> generosFiltro) {
        if (filme.getGeneros() == null) return false;
        return filme.getGeneros().stream()
                .map(MovieSearchCriteria::normalizar)
                .anyMatch(generosFiltro::contains);
    }

    private boolean matchDuracao(Movie filme, List<String> duracoesFiltro) {
        if (filme.getDuracao() == null) return false;
        int d = filme.getDuracao();
        return duracoesFiltro.stream().anyMatch(faixa -> switch (faixa) {
            case "CURTA" -> d <= 60;
            case "MEDIA" -> d >= 61 && d <= 110;
            case "LONGA" -> d > 110;
            default -> false;
        });
    }

    private boolean matchDecada(Movie filme, List<Integer> decadasFiltro) {
        if (filme.getAnoLancamento() == null) return false;
        int decadaFilme = (filme.getAnoLancamento() / 10) * 10;
        return decadasFiltro.contains(decadaFilme);
    }

    private boolean estaVazio(List<?> lista) {
        return lista == null || lista.isEmpty();
    }

    private <T> T withMongoFallback(Supplier<T> mongoCall, Supplier<T> fallbackCall, String operation) {
        try {
            return mongoCall.get();
        } catch (Exception ex) {
            if (!localFallbackEnabled) {
                log.error("Falha no Mongo durante {}. Fallback local em memória desativado. Motivo: {}", operation, ex);
                throw ex;
            }
            log.warn("Falha no Mongo durante {}. Ativando fallback local em memória. Motivo: {}", operation, ex.getMessage());
            return fallbackCall.get();
        }
    }
}
