package br.com.blade.indicafilme.repository;

import br.com.blade.indicafilme.model.Movie;
import br.com.blade.indicafilme.model.StatusFilme;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("!local")
public interface MongoMovieRepository extends MongoRepository<Movie, String> {
    List<Movie> findByStatus(StatusFilme status);
    Optional<Movie> findByTituloIgnoreCase(String titulo);
}
