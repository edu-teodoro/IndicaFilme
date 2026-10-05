package br.com.blade.indicafilme.repository;

import br.com.blade.indicafilme.model.Movie;
import br.com.blade.indicafilme.model.StatusFilme;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("!local")
public class MongoMovieRepositoryAdapter implements MovieRepository {

    private final MongoMovieRepository repository;

    public MongoMovieRepositoryAdapter(MongoMovieRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Movie> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Movie> findByStatus(StatusFilme status) {
        return repository.findByStatus(status);
    }

    @Override
    public Optional<Movie> findByTituloIgnoreCase(String titulo) {
        return repository.findByTituloIgnoreCase(titulo);
    }

    @Override
    public Optional<Movie> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Movie save(Movie movie) {
        return repository.save(movie);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
