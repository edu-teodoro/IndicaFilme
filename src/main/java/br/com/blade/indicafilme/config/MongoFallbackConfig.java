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
 * <p>
 * Quando o MongoDB Atlas está disponível, o Spring Data cria automaticamente a implementação do {@link MovieRepository}.
 * Quando a conexão com o MongoDB falha (ex: sem internet, serviço indisponível), esta configuração define o {@link InMemoryMovieRepository}
 * como fallback, utilizando os dados de {@link FilmeData} para manter a aplicação funcional.
 * </p>
 *
 * <p>
 * <b>Resumo:</b><br>
 * - Se o MongoDB estiver disponível, o repositório padrão será utilizado.<br>
 * - Se o MongoDB estiver indisponível, um repositório em memória será utilizado como fallback.<br>
 * - Os dados em memória são provenientes de {@link FilmeData}.<br>
 * </p>
 *
 * @author SeuNome
 * @since 1.0
 */

@Configuration
public class MongoFallbackConfig {

    private static final Logger log = LoggerFactory.getLogger(MongoFallbackConfig.class);

    /**
     * Cria um bean de fallback para o repositório de filmes caso o MongoDB esteja indisponível.
     * <p>
     * Este método será chamado automaticamente pelo Spring quando não houver um {@link MongoTemplate} disponível no contexto,
     * indicando que o MongoDB não está acessível. Neste caso, um repositório em memória será utilizado para garantir o funcionamento da aplicação.
     * </p>
     *
     * @return uma instância de {@link InMemoryMovieRepository} para ser utilizada como fallback
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
