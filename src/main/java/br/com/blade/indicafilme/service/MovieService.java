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
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

/**
 * Serviço responsavel pelas regras de negócio de filmes.
 *
 * Realiza filtragem, sorteio (com suporte a exclusão do último filme visto),
 * busca por ID e enriquecimento com nota pública via {@link TmdbService}
 *
 * A fonte de dados é abstraida pelo {@link MovieRepository} - em produção usa MongoDB,
 * em fallback usa repositorios em memória ({@link InMemoryMovieRepository})
 */
@Service
public class MovieService {

    private static final Logger log = LoggerFactory.getLogger(MovieService.class);

    private final MovieRepository movieRepository;
    private final TmdbService tmdbService;
    private final SecureRandom random = new SecureRandom();

    /**
     * Construtor com injeção de dependencias.
     *
     * @param movieRepository   repo de filmes.
     * @param tmdbService       serviço de integração com o TMDB para nota pública.
     */
    @Autowired
    public MovieService(MovieRepository movieRepository, TmdbService tmdbService){
        this.movieRepository = movieRepository;
        this.tmdbService = tmdbService;
    }

    /**
     * Filtra filmes com base nos critérios informados.
     *
     * Regras de filtragem
     *      Apenas filmes com status {@link StatusFilme#ATIVO} são retornados.
     *      Ao menos um critério deve ser informado.
     *      Um filme aparece se corresponder a qualquer valor de cada filtro
     *      Se multiplos tipos de filtro forem informados, todos devem ser satisfatorios
     *
     * @param filtro cirterios de busca ja normalizados
     * @return lista de filmes que correspondem aos criterios.
     * @throws BadRequestException se nenhum criterio for informado
     */
    public List<Movie> filterMovies(MovieSearchCriteria filtro) {
        boolean semFiltro = estaVazio(filtro.generos())
                && estaVazio(filtro.duracoes())
                && estaVazio(filtro.decadas());

        if (semFiltro){
            log.warn("Busca sem nenhum filtro informado");
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
    /**
     * Sorteia aleatoriamente um filme de uma lista de candidatos,
     * evitando repetir o último filme exibido
     *
     * Lógica de cache:
     *      Remove o filme {@code excludeId} da lista de candidatos.
     *      Se sobrar ao menos um candidato diferente, sorteia entre eles.
     *      Se a lista reduzida ficar vazia (todos os candidatos são o mesmo filme),
     *      usa a lista original (melhor que não retornar nada)
     *
     * @param candidatos lista de filmes candidatos.
     * @param excludeId ID do filme a exluir (último visto). Pode ser {@code null}
     * @return {@link Optional} com o filme sorteado, ou vazio se a lista for nulta/vazia
     */
    public Optional<Movie> pickRandom(List<Movie> candidatos, String excludeId) {
        if (candidatos == null || candidatos.isEmpty()) return Optional.empty();

        List<Movie> elegíveis = candidatos;

        if (excludeId != null && !excludeId.isBlank()){
            List<Movie> semUltimo = candidatos.stream()
                    .filter(f -> !excludeId.equals(f.getId()))
                    .toList();
            // Só usa a lista reduzida se ainda houver candidatos
            if (!semUltimo.isEmpty()){
                elegíveis = semUltimo;
                log.debug("Cache: excluindo filme '{}' do sorteio ({} candidato(s) restantes)",
                        excludeId, elegíveis.size());
            } else {
                log.info("Cache: todos os {} candidato(s) são o mesmo filme - usando lista completa", candidatos.size());
            }
        }

        Movie escolhido = elegíveis.get(random.nextInt(elegíveis.size()));
        log.info("Filme sorteado: '{}", escolhido.getTitulo());
        return Optional.of(escolhido);
    }

    /**
     * Sorteia aleatoriamente um filme sem excluir nenhum (compativilidade retroativa).
     *
     * @param candidatos lista de filmes candidatos
     * @return {@link Optional} com o filme sorteado, ou vazio se a lista for nula/vazia
     */
    public Optional<Movie> pickRandom(List<Movie> candidatos){
        return pickRandom(candidatos, null);
    }

    /**
     * Busca um filme pelo seu ID e enriquece a nota pública via TMDB.
     *
     * Se o filme não tiver nota pública cadastrada, tenta buscar no TMDB.
     * Se o TMDB falhar, mantém o valor original (nulo ou cadastrado).
     *
     * @param id identificador do filme.
     * @return {@link Optional} com o filme enriquecido, ou vazio se não encontrado
     */
    public Optional<Movie> findById(String id){
        log.debug("findById: id={}", id);
        return movieRepository.findById(id)
                .map(this::enriquecerComTmdb);
    }

    /**
     * Busca um filme pelo título
     *
     * @param titulo titulo do filme do MongoDB
     * @return {@link Optional} com o titulo do filme
     */
    public Optional<Movie> findByTitle(String titulo) {
        log.debug("findByTitle: titulo={}", titulo);
        return movieRepository.findByTituloIgnoreCase(titulo);
    }

    /**
     * Enriquece um filme com a nota publica do TMDB, se ainda nao tiver.
     *
     * Só realiza a chamada ao TMDB se {@code notaPublica} for nula,
     * evitando requisições desnecessarias para filmes ja com nota cadastrada.
     *
     * @param filme filme a enriquecer
     * @return filme com nota publica preenchida (se disponivel no TMDB)
     */public Movie enriquecerComTmdb(Movie filme) {
         if (filme.getNotaPublico() == null && filme.getTitulo() != null){
             tmdbService.buscarNotaPublico(filme.getTitulo(), filme.getAnoLancamento())
                     .ifPresent(nota -> {
                         filme.setNotaPublico(nota);
                         log.debug("Nota TMDB aplicada ao filme '{}': {}", filme.getTitulo(), nota);
                     });
         }
         return filme;
    }

    public Movie save(Movie filme){
         Movie salvo = movieRepository.save(filme);
         log.info("Filme salvo: '{}' (id={}", salvo.getTitulo(), salvo.getId());
         return salvo;
    }

    public Movie atualizarStatus(String id, StatusFilme status) {
         Movie filme = movieRepository.findById(id)
                 .orElseThrow(() -> new NotFoundException("Filme não encontrado: " + id));

         StatusFilme statusAnterior = filme.getStatus();
         filme.setStatus(status);
         Movie salvo = movieRepository.save(filme);

         log.info("Status do filme '{}' (id={}) alterado de {} para {}",
                 salvo.getTitulo(), salvo.getId(), statusAnterior, status);
         return salvo;
    }

    public void deleteById(String id) {
         log.info("Deletando filme id={}", id);
         movieRepository.deleteById(id);
    }

    private boolean matchGenero(Movie filme, List<String> generosFiltro){
         if (filme.getGeneros() == null) return false;
         return filme.getGeneros().stream()
                 .map(MovieSearchCriteria::normalizar)
                 .anyMatch(generosFiltro::contains);
    }

    private boolean matchDuracao(Movie filme, List<String> duracoesFiltro) {
         if (filme.getDuracao() == null) return false;
         int d = filme.getDuracao();
         return duracoesFiltro.stream().anyMatch(faixa -> switch (faixa){
             case "CURTA" -> d <=60;
             case "MEDIA" -> d >=61 && d <=110;
             case "LONGA" -> d > 110;
             default -> false;
         });
    }

    private boolean matchDecada(Movie filme, List<Integer> decadasFiltro){
         if (filme.getAnoLancamento() == null) return false;
         int decadaFilme = (filme.getAnoLancamento() / 10) *10;
         return decadasFiltro.contains(decadaFilme);
    }

    /** Retorna {@code true} se a lista for nula ou vazia - significa "sem filtro desse tipo". */
    private boolean estaVazio(List<?> lista) {
        return lista == null || lista.isEmpty();
    }
}




















