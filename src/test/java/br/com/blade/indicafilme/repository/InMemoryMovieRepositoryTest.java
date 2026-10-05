package br.com.blade.indicafilme.repository;

import br.com.blade.indicafilme.model.Movie;
import br.com.blade.indicafilme.model.StatusFilme;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryMovieRepositoryTest {

    @Test
    void iniciaComCatalogoEPermiteCrud() {
        InMemoryMovieRepository repository = new InMemoryMovieRepository();
        long totalInicial = repository.count();

        Movie movie = new Movie();
        movie.setTitulo("Filme de teste");
        movie.setStatus(StatusFilme.ATIVO);

        Movie salvo = repository.save(movie);

        assertEquals(totalInicial + 1, repository.count());
        assertTrue(repository.findById(salvo.getId()).isPresent());

        repository.deleteById(salvo.getId());

        assertFalse(repository.findById(salvo.getId()).isPresent());
        assertEquals(totalInicial, repository.count());
    }
}
