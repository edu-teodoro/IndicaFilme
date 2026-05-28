package br.com.blade.indicafilme.controller;

import br.com.blade.indicafilme.dto.MovieDto;
import br.com.blade.indicafilme.dto.MovieSearchCriteria;
import br.com.blade.indicafilme.exception.BadRequestException;
import br.com.blade.indicafilme.exception.NotFoundException;
import br.com.blade.indicafilme.model.Movie;
import br.com.blade.indicafilme.service.MovieCacheService;
import br.com.blade.indicafilme.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;

/**
 * Controller responsavel pelos endpoints publicos de filmes
 *
 * Todas as rotas começam com {@code /api/v1/movies}
 *
 * Regras gerais:
 *      Apenas filmes com status {@code ATIVO} são retornados
 *      Filtros são case-insensitive e ignoram acentos
 *      Cada filtro aceita multiplos valores (até 3 por tipo)
 *      Se a lista filtrada estiver vazia, retorna a String {@code "XXX"}
 *      O sorteio evita repetir o ultimo filme exibido na mesma sessao (cache de sessão)
 *      Cadastro de filmes é feito pelos endpoints admin ({@code /api/v1/admin/movies})
 */
@RestController
@RequestMapping("/api/v1/movies")
@Tag(name = "Filmes", description = "Endpoints públicos para busca e sorteio de filmes")
public class MovieController {

    private static final Logger log = LoggerFactory.getLogger(MovieController.class);

    /** Mensagem retornada quando nenhum filme é encontrado. */
    private static final String LISTA_VAZIA = "Desculpe, não encontramos nenhum filme que corresponda aos filtros solicitados. Que tal tentar outros?";

    private final MovieService service;
    private final MovieCacheService cacheService;


    /**
     * Construtor com injeção de dependências.
     *
     * @param service       serviço de regras de negócio de filmes.
     * @param cacheService  serviço de cache de sessão para controle de repetição.
     */
    @Autowired
    public MovieController(MovieService service, MovieCacheService cacheService) {
        this.service = service;
        this.cacheService = cacheService;
    }

    /**
     * GET /api/v1/movies/random - Sorteia um filme aleatório com base nos filtros informados.
     *
     * Sorteia aleatoriamente um filme com base nos filtros informados.
     *
     * Utiliza o cache da sessão para evitar que o mesmo filme seja
     * retornado consecutivamente. Se o user clicar em "Sortear" novamente,
     * o sistema exclui o filme exibido da lista de candidatos.
     *
     *
     * @param generos lista de gêneros (ex: genero=Ação&amp;genero=Drama).
     * @param duracoes lista de faixas: {@code CURTA},{@code MEDIA},{@code LONGA}.
     * @param decadas lista de anos (ex: decada=1990&amp;decada=2000). Qualquer ano da década é aceito.
     * @param request requisição HTTP (usada para obter a sessão do usuário).
     * @return {@code 200 OK} com o filme sorteado, ou mensagem se nenhum for encontrado.
     */
    @Operation(summary = "Sortear filme", description = "Sorteia aleatoriamente um filme que corresponde aos filtros informados. " +
                                                        "Usa cache de sessão para evitar repetir o último filme exibido."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Filme sorteado com sucesso ou mensagem se lista vazia"),
            @ApiResponse(responseCode = "400", description = "Nenhum filtro informado ou filtro inválido")
    })
@GetMapping("/random")
    public ResponseEntity<?> sortearFilme(
            @Parameter(description = "Gêneros do filme (case-insensitive). Ex: Ação, Drama")
            @RequestParam(required = false) List<String> generos,
            @Parameter(description = "Faixa de duração: CURTA (<=60min), MEDIA (61 - 110min), LONGA (>110min)")
            @RequestParam(required = false) List<String> duracoes,
            @Parameter(description = "Década de lançamento. Ex: 1990, 2000. Anos como 1995 são convertidos para 1990")
            @RequestParam(required = false) List<Integer> decadas,
            HttpServletRequest request) {

        log.info("GET /random -> generos={}, duracoes={}, decadas={}", generos, duracoes, decadas);

        validarDecadas(decadas);

        MovieSearchCriteria criteria = new MovieSearchCriteria(generos, duracoes, decadas);
        List<Movie> candidatos = service.filterMovies(criteria);

        if (candidatos.isEmpty()) {
            log.info("Nenhum filme encontrado para os filtros informados");
            return ResponseEntity.ok(LISTA_VAZIA);
        }

        // Obtém o ID do último filme exibido nesta sessão (pode ser null)
        HttpSession session = request.getSession(true);
        String ultimoId = cacheService.getUltimoFilmeId(session.getId());

        return service.pickRandom(candidatos, ultimoId)
                .map(filme -> {
                    // Registra o filme sorteado no cache da sessão
                    cacheService.registrarUltimoFilme(session.getId(), filme.getId());
                    // Enriquece com nota do TMDb antes de retornar
                    service.enriquecerComTmdb(filme);
                    return ResponseEntity.ok((Object) MovieDto.fromMovie(filme));
                })
                .orElseGet(() -> ResponseEntity.ok(LISTA_VAZIA));
    }

    /**
     * GET /api/v1/movies - Lista TODOS os filmes com base nos filtros informados.
     *
     * @param generos lista de gêneros.
     * @param duracoes lista de faixa de duração.
     * @param decadas lista de décadas.
     * @return {@code 200 OK} com a lista de filmes filtrada, ou mensagem se nenhum for encontrado.
     */
    @Operation(summary = "Listar filmes", description = "Lista todos os filmes que correspondem aos filtros informados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de filmes filtrada com sucesso ou mensagem se lista vazia"),
        @ApiResponse(responseCode = "400", description = "Nenhum filtro informado ou filtro inválido")
    })
    @GetMapping
    public ResponseEntity<?> listarFilmes(
            @Parameter(description = "Gêneros do filme (case-insensitive)")
            @RequestParam(required = false) List<String> generos,
            @Parameter(description = "Faixa de duração: CURTA, MEDIA, LONGA")
            @RequestParam(required = false) List<String> duracoes,
            @Parameter(description = "Década de lançamento. (Ex: 1990, 2000)")
            @RequestParam(required = false) List<Integer> decadas) {

        log.info("GET /movies -> generos={}, duracoes={}, decadas={}", generos, duracoes, decadas);

        validarDecadas(decadas);

        MovieSearchCriteria criteria = new MovieSearchCriteria(generos, duracoes, decadas);
        List<MovieDto> resultado = service.filterMovies(criteria)
                .stream()
                .map(MovieDto::fromMovie)
                .toList();

        if (resultado.isEmpty()) {
            log.info("Nenhum filme encontrado para os filtros informados");
            return ResponseEntity.ok(LISTA_VAZIA);
        }

        return ResponseEntity.ok(resultado);
    }

    /**
     * GET /api/v1/movies/{id} - Busca um filme por ID.
     *
     * Busca um filme pelo seu identificador único
     *
     * Enriquece a nota pública via TMDB se não estiver cadastrada.
     *
     * @param id identificador do filme.
     * @return {@code 200 OK} com os detalhes do filme, ou {@code 404 Not Found} se não encontrado.
     */
    @Operation(summary = "Buscar filme por ID", description = "Retorna os detalhes de um filme especifico, enriquecido com nota do TMDb.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Filme encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Filme não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> buscarPorId(
            @Parameter(description = "ID do filme no MongoDB")
            @PathVariable String id) {
        log.info("GET /movies/{}", id);
        return service.findById(id)
                .map(MovieDto::fromMovie)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Filme não encontrado com ID: " + id));
    }

    /**
     * GET /api/v1/movies/{titulo} - Busca um filme por título.
     *
     * Busca um filme pelo seu título.
     *
     * @param titulo título do filme.
     * @return {@code 200 OK} com os detalhes do filme, ou {@code 404 Not Found} se não encontrado.
     */
    @Operation(summary = "Buscar filmes por título", description = "Retorna os detalhes de um filme específico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Filme encontrado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Filme não encontrado")
    })
    @GetMapping("/titulo/{titulo}")
    public ResponseEntity<MovieDto> buscarPorTitulo(
            @Parameter(description = "Título do filme no MongoDB")
            @PathVariable String titulo) {
        log.info("GET /movies/titulo/{}", titulo);
        return service.findByTitle(titulo)
                .map(MovieDto::fromMovie)
                .map(dto -> ResponseEntity.ok(dto))
                .orElseThrow(() -> new NotFoundException("Filme não encontrado: " + titulo));
    }

    /**
     * Validação privada
     *
     * Valida que cada década informada está entre 1900 e a década atual.
     * Valores como 1995 são aceitos e convertidos para 1990 pelo {@link MovieSearchCriteria}.
     * Décadas futuras (ex: 2030 quando o ano atual é 2026) são rejeitadas.
     *
     * @param decadas lista de décadas a validar.
     * @throws BadRequestException se alguma década for inválida.
     */
    private void validarDecadas(List<Integer>  decadas) {
        if (decadas == null) return;
        int limiteMax = (Year.now().getValue() / 10) * 10; // Ex: 2024 -> 2020
        decadas.forEach(d -> {
        if (d < 1900 || d > limiteMax) {
            throw new BadRequestException(
                    "Década " + d + " inválida. Use valores entre 1900 e " + limiteMax + " (ex: 1990, 1995, 2000)."
            );
        }
        });
    }
}