package br.com.blade.indicafilme.repository;

import br.com.blade.indicafilme.model.Movie;
import br.com.blade.indicafilme.model.StatusFilme;

import java.util.List;
import java.util.Optional;

public interface MovieRepository {
    List<Movie> findAll();
    List<Movie> findByStatus(StatusFilme status);
    Optional<Movie> findByTituloIgnoreCase(String titulo);    
    Optional<Movie> findById(String id);
    Movie save(Movie movie);
    long count();
    void deleteById(String id);
}
