package br.com.blade.indicafilme.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propriedades de config do cliente TMDB
 *
 * Os valores são lidos automaticamente do {@code application.yml} pelo Spring Boot.
 * Prefixo: {@code tmdb}.
 *
 * Ex de config no YAML:
 * tmdb:
 *      api-key: minha_chave_aqui
 *      base-url: https://api.themoviedb.org/3
 */
@Component
@ConfigurationProperties(prefix = "tmdb")
public class TmdbProperties {

    private String apiKey;
    private String baseUrl = "https://api.themoviedb.org/3";

    public String getApiKey() { return apiKey; }

    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getBaseUrl() { return baseUrl; }

    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
}
