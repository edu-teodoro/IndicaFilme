package br.com.blade.indicafilme.repository;

import br.com.blade.indicafilme.config.MongoFallbackConfig;
import br.com.blade.indicafilme.model.Movie;
import br.com.blade.indicafilme.model.StatusFilme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Implementação em memória do {@link MovieRepository}
 * 
 * <p>Utilizada em dois cenários:</p>
 * <ol>
 * 
 */
public class InMemoryMovieRepository implements MovieRepository {

    private static final Logger log = LoggerFactory.getLogger(InMemoryMovieRepository.class);

    private final List<Movie> filmes = FilmeData.todos();

    @Override
    public List<Movie> findAll() {
        log.debug("InMemory: retornando {} filme(s)", filmes.size());
        return filmes;
    }

    @Override
    public Optional<Movie> findById(String id) {
        return filmes.stream().filter(f -> f.getId().equals(id)).findFirst();
    }

    @Override
    public List<Movie> findByStatus(StatusFilme status) {
        return filmes.stream().filter(f -> f.getStatus() == status).toList();
    }

    @Override
    public Optional<Movie> findByTituloIgnoreCase(String titulo) {
        if (titulo == null)
            return Optional.empty();
        return filmes.stream()
                .filter(f -> titulo.equalsIgnoreCase(f.getTitulo()))
                .findFirst();
    }

    // Métodos obrigatórios da interface MongoRepository
    // Implementações mínimas para satisfazer o contrato

    @Override
    public <S extends Movie> S save(S entity) {
        return entity;
    }

    @Override
    public <S extends Movie> List<S> saveAll(Iterable<S> entities) {
        return (List<S>) entities;
    }

    @Override
    public boolean existsById(String id) {
        return findById(id).isPresent();
    }

    @Override
    public List<Movie> findAllById(Iterable<String> ids) {
        return List.of();
    }

    @Override
    public long count() {
        return filmes.size();
    }

    @Override
    public void deleteById(String id) {
    }

    @Override
    public void delete(Movie entity) {
    }

    @Override
    public void deleteAllById(Iterable<? extends String> ids) {
    }

    @Override
    public void deleteAll(Iterable<? extends Movie> entities) {
    }

    @Override
    public void deleteAll() {
    }

    @Override
    public List<Movie> findAll(Sort sort) {
        return filmes;
    }

    @Override
    public Page<Movie> findAll(Pageable pageable) {
        return Page.empty();
    }

    @Override
    public <S extends Movie> S insert(S entity) {
        return entity;
    }

    @Override
    public <S extends Movie> List<S> insert(Iterable<S> entities) {
        return (List<S>) entities;
    }

    @Override
    public <S extends Movie> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Movie> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Movie> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Movie> Page<S> findAll(Example<S> example, Pageable pageable) {
        return Page.empty();
    }

    @Override
    public <S extends Movie> Long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Movie> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Movie, R> R findBy(Example<S> example,
            Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }
}
