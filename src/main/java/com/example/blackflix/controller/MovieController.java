package com.example.blackflix.controller;

import com.example.blackflix.dto.MovieSearchCriteria;
import com.example.blackflix.model.Movie;
import com.example.blackflix.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MovieController {

    private final MovieService service;

    @Autowired
    public MovieController(MovieService service) {
        this.service = service;
    }

    @GetMapping("/movies")
    public ResponseEntity<?> searchMovies(@RequestParam(required = false) String genero,
                                          @RequestParam(required = false) String duracao,
                                          @RequestParam(required = false) Integer decada,
                                          @RequestParam(required = false, defaultValue = "false") boolean random) {
        MovieSearchCriteria criteria = new MovieSearchCriteria();
        criteria.setGenero(genero);
        criteria.setDuracao(duracao);
        criteria.setDecada(decada);

        List<Movie> found;
        try {
            found = service.filterMovies(criteria);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }

        if (found.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        if (random) {
            return ResponseEntity.ok(service.pickRandom(found).orElse(null));
        }

        return ResponseEntity.ok(found);
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return service.findById(id)
                .map(movie -> ResponseEntity.ok(movie))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Filme não encontrado"));
    }

    @PostMapping("/movies")
    public ResponseEntity<?> createMovie(@RequestBody Movie movie) {
        Movie saved = service.save(movie);
        return ResponseEntity.created(URI.create("/api/movies/" + saved.getId())).body(saved);
    }

    @PostMapping("/suggestions")
    public ResponseEntity<?> suggestMovie(@RequestBody Movie suggestion) {
        // Para simplificar, armazenar sugestão na mesma coleção (ou criar outra coleção caso queira)
        Movie saved = service.save(suggestion);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(saved);
    }
}
