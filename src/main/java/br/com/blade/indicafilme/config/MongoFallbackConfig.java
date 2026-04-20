package br.com.blade.indicafilme.config;

import br.com.blade.indicafilme.repository.FilmeData;
import br.com.blade.indicafilme.repository.InMemoryMovieRepository;
import br.com.blade.indicafilme.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Configuração de fallback para o repositório de filmes.
 * 
 * Quando o MongoDB Atlas está disponível, o Spring Data cria automaticamente a implementação do {@link MovieRepository}.
 * Quando a conexão com o MongoDB falha (ex: sem internet, serviço indisponível), esta configuração define o {@link InMemoryMovieRepository}
 * como fallback, utilizando os dados de {@link FilmeData}.
 *
 * A detecção de falha é feita pelo {@link MongoConnectionHealthIndicator}
 * ao iniciar a aplicação. O resultado define qual repositório é ativo
 */

@Configuration
public class MongoFallbackConfig {

    private static final Logger log = LoggerFactory.getLogger(MongoFallbackConfig.class);

    /**
     * Registra o {@link InMemoryMovieRepository} como bean fallback.
     *
     * O {@code @ConditionalOnMissingBean} garante que este bean só é criado
     * se o Spring Data MongoDB NÂO conseguir criar sua implementação automática.
     * Na prática, isso acontece quando o MongoDB está indisponível.
     *
     * Nota: em produção com MongoDB disponível, este bean NÂO é criado.
     *
     * @return repositório em memória com filmes do {@link FilmeData}.
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(MongoTemplate.class)
    public MovieRepository inMemoryMovieRepositoryFallback() {
        log.warn("=======================================================");
        log.warn(" MongoDB INDISPONÍVEL - usando base de filmes em memória.");
        log.warn(" Dados do FilmeData.java estão sendo utilizados.");
        log.warn("=======================================================");
        return new InMemoryMovieRepository();
    }
}
