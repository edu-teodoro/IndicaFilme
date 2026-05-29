package br.com.blade.indicafilme.service;

import br.com.blade.indicafilme.dto.MovieSearchCriteria;
import br.com.blade.indicafilme.exception.BadRequestException;
import br.com.blade.indicafilme.exception.NotFoundException;
import br.com.blade.indicafilme.model.Movie;
import br.com.blade.indicafilme.model.StatusFilme;
import br.com.blade.indicafilme.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private static final Logger log = LoggerFactory.getLogger(MovieService.class);

    private final MovieRepository movieRepository;
    private final TmdbService tmdbService;
    private final SecureRandom random = new SecureRandom();

    @Autowired
    public MovieService(MovieRepository movieRepository, TmdbService tmdbService) {
        this.movieRepository = movieRepository;
        this.tmdbService = tmdbService;
    }

    public List<Movie> filterMovies(MovieSearchCriteria filtro) {
        boolean semFiltro = estaVazio(filtro.generos())
                && estaVazio(filtro.duracoes())
                && estaVazio(filtro.decadas());

        if (semFiltro) {
            throw new BadRequestException("Ao menos um filtro deve ser informado.");
        }

        List<Movie> resultado = movieRepository.findByStatus(StatusFilme.ATIVO).stream()
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
        log.info("Filme sorteado: '{}", escolhido.getTitulo());
        return Optional.of(escolhido);
    }

    public Optional<Movie> pickRandom(List<Movie> candidatos) {
        return pickRandom(candidatos, null);
    }

    public Optional<Movie> findById(String id) {
        return movieRepository.findById(id).map(this::enriquecerComTmdb);
    }

    public Optional<Movie> findByTitle(String titulo) {
        return movieRepository.findByTituloIgnoreCase(titulo);
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
        log.info("Filme salvo: '{}' (id={}", salvo.getTitulo(), salvo.getId());
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
}
