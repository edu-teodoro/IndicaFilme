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

@RestController
@RequestMapping("/api/v1/movies")
@Tag(name = "Filmes", description = "Endpoints públicos para busca e sorteio de filmes")
public class MovieController {

    private static final Logger log = LoggerFactory.getLogger(MovieController.class);
    private static final String LISTA_VAZIA = "Desculpe, não encontramos nenhum filme que corresponda aos filtros solicitados. Que tal tentar outros?";

    private final MovieService service;
    private final MovieCacheService cacheService;

    @Autowired
    public MovieController(MovieService service, MovieCacheService cacheService) {
        this.service = service;
        this.cacheService = cacheService;
    }

    @Operation(summary = "Sortear filme")
    @GetMapping("/random")
    public ResponseEntity<?> sortearFilme(
            @Parameter(description = "Gêneros") @RequestParam(required = false) List<String> generos,
            @Parameter(description = "Duração: CURTA, MEDIA, LONGA") @RequestParam(required = false) List<String> duracoes,
            @Parameter(description = "Década. Ex: 1990, 2000") @RequestParam(required = false) List<Integer> decadas,
            HttpServletRequest request) {

        log.info("GET /random -> generos={}, duracoes={}, decadas={}", generos, duracoes, decadas);
        validarDecadas(decadas);

        MovieSearchCriteria criteria = new MovieSearchCriteria(generos, duracoes, decadas);
        List<Movie> candidatos = service.filterMovies(criteria);

        if (candidatos.isEmpty()) {
            return ResponseEntity.ok(LISTA_VAZIA);
        }

        HttpSession session = request.getSession(true);
        String ultimoId = cacheService.getUltimoFilmeId(session.getId());

        return service.pickRandom(candidatos, ultimoId)
                .map(filme -> {
                    cacheService.registrarUltimoFilme(session.getId(), filme.getId());
                    service.enriquecerComTmdb(filme);
                    return ResponseEntity.ok((Object) MovieDto.fromMovie(filme));
                })
                .orElseGet(() -> ResponseEntity.ok(LISTA_VAZIA));
    }

    @Operation(summary = "Listar filmes")
    @GetMapping
    public ResponseEntity<?> listarFilmes(
            @RequestParam(required = false) List<String> generos,
            @RequestParam(required = false) List<String> duracoes,
            @RequestParam(required = false) List<Integer> decadas) {

        log.info("GET /movies -> generos={}, duracoes={}, decadas={}", generos, duracoes, decadas);
        validarDecadas(decadas);

        MovieSearchCriteria criteria = new MovieSearchCriteria(generos, duracoes, decadas);
        List<MovieDto> resultado = service.filterMovies(criteria).stream()
                .map(MovieDto::fromMovie)
                .toList();

        return resultado.isEmpty() ? ResponseEntity.ok(LISTA_VAZIA) : ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Buscar filme por ID")
    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> buscarPorId(@PathVariable String id) {
        log.info("GET /movies/{}", id);
        return service.findById(id)
                .map(MovieDto::fromMovie)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Filme não encontrado com ID: " + id));
    }

    @Operation(summary = "Buscar filmes por título")
    @GetMapping("/titulo/{titulo}")
    public ResponseEntity<MovieDto> buscarPorTitulo(@PathVariable String titulo) {
        log.info("GET /movies/titulo/{}", titulo);
        return service.findByTitle(titulo)
                .map(MovieDto::fromMovie)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Filme não encontrado: " + titulo));
    }

    private void validarDecadas(List<Integer> decadas) {
        if (decadas == null) return;
        int limiteMax = (Year.now().getValue() / 10) * 10;
        decadas.forEach(d -> {
            if (d < 1900 || d > limiteMax) {
                throw new BadRequestException(
                        "Década " + d + " inválida. Use valores entre 1900 e " + limiteMax + " (ex: 1990, 1995, 2000).");
            }
        });
    }
}