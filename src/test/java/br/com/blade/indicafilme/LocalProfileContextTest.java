package br.com.blade.indicafilme;

import br.com.blade.indicafilme.repository.InMemoryMovieRepository;
import br.com.blade.indicafilme.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("local")
class LocalProfileContextTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MovieRepository movieRepository;

    @Test
    void iniciaSemMongoTemplateEUsaRepositorioEmMemoria() {
        assertInstanceOf(InMemoryMovieRepository.class, movieRepository);
        assertFalse(applicationContext.containsBean("mongoTemplate"));
    }
}
