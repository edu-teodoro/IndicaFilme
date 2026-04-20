package br.com.blade.indicafilme.config;

import br.com.blade.indicafilme.service.TmdbService;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Config de beans de infra HTTP
 *
 * Declara o {@link RestTemplate} como um bean Spring gerenciado,
 * permitindo sua injeção em serviços como o {@link TmdbService}.
 *
 * Configura timeouts para evitar que chamadas à API do TMDB
 * fiquem travadas indefinidamente
 */
@Configuration
public class HttpClientConfig {

    /**
     * Cria um {@link RestTemplate} com timeouts configurados.
     *
     *      Conexão: 3 segundos
     *      Leitura: 5 segundos
     *
     * @param builder builder fornecido pelo Spring Boot
     * @return instância de {@link RestTemplate} com timeouts
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofSeconds(3))
                .readTimeout(Duration.ofSeconds(5))
                .build();
    }
}