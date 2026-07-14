package br.com.blade.indicafilme.config;

import br.com.blade.indicafilme.repository.InMemoryMovieRepository;
import br.com.blade.indicafilme.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoFallbackConfig {

    private static final Logger log = LoggerFactory.getLogger(MongoFallbackConfig.class);

    @Bean
    @ConditionalOnMissingBean(MongoTemplate.class)
    @ConditionalOnProperty(prefix = "app.fallback", name = "local-enabled", havingValue = "true")
    public MovieRepository inMemoryMovieRepositoryFallback() {
        log.warn(" MongoDB INDISPONÍVEL - usando base de filmes em memória.");
        return new InMemoryMovieRepository();
    }
}
