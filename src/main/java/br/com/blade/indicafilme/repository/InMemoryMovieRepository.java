package br.com.blade.indicafilme.repository;

import br.com.blade.indicafilme.model.Movie;
import br.com.blade.indicafilme.model.StatusFilme;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryMovieRepository implements MovieRepository {

    private final List<Movie> filmes = new ArrayList<>(FilmeData.todos());
    private final AtomicLong proximoId = new AtomicLong(calcularProximoId());

    @Override public List<Movie> findAll() { return List.copyOf(filmes); }
    @Override public Optional<Movie> findById(String id) {
        return filmes.stream().filter(f -> id != null && id.equals(f.getId())).findFirst();
    }
    @Override public List<Movie> findByStatus(StatusFilme status) { return filmes.stream().filter(f -> f.getStatus() == status).toList(); }
    @Override public Optional<Movie> findByTituloIgnoreCase(String titulo) {
        if (titulo == null) return Optional.empty();
        return filmes.stream().filter(f -> titulo.equalsIgnoreCase(f.getTitulo())).findFirst(); 
    }

    @Override public Movie save(Movie entity) {
        if (entity.getId() == null || entity.getId().isBlank()) {
            entity.setId(String.valueOf(proximoId.getAndIncrement()));
            filmes.add(entity);
            return entity;
        }

        for (int index = 0; index < filmes.size(); index++) {
            if (entity.getId().equals(filmes.get(index).getId())) {
                filmes.set(index, entity);
                return entity;
            }
        }

        filmes.add(entity);
        return entity;
    }
    @Override public long count() { return filmes.size(); }
    @Override public void deleteById(String id) { filmes.removeIf(f -> id != null && id.equals(f.getId())); }

    private long calcularProximoId() {
        return filmes.stream()
                .map(Movie::getId)
                .filter(id -> id != null && id.matches("\\d+"))
                .mapToLong(Long::parseLong)
                .max()
                .orElse(0L) + 1;
    }
}
