package br.com.blade.indicafilme.service;

import br.com.blade.indicafilme.config.TmdbProperties;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class TmdbService {

    private static final Logger log = LoggerFactory.getLogger(TmdbService.class);

    private final TmdbProperties tmdbProperties;
    private final RestTemplate restTemplate;

    private final Cache<String, Optional<Double>> tmdbCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .maximumSize(500)
            .build();

    public TmdbService(TmdbProperties tmdbProperties, RestTemplate restTemplate) {
        this.tmdbProperties = tmdbProperties;
        this.restTemplate = restTemplate;
    }

    public Optional<Double> buscarNotaPublico(String titulo, Integer anoLancamento) {
        String apiKey = tmdbProperties.getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            return Optional.empty();
        }
        String cacheKey = titulo + "_" + anoLancamento;
        return tmdbCache.get(cacheKey, k -> buscarNotaPublicoInterno(titulo, anoLancamento));
    }

    @SuppressWarnings("unchecked")
    private Optional<Double> buscarNotaPublicoInterno(String titulo, Integer anoLancamento) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(tmdbProperties.getBaseUrl() + "/search/movie")
                    .queryParam("query", titulo)
                    .queryParam("language", "pt-BR");

            if (anoLancamento != null) {
                builder.queryParam("year", anoLancamento);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(tmdbProperties.getApiKey());
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> responseEntity = restTemplate.exchange(
                    builder.build().toUriString(), HttpMethod.GET, entity, Map.class);
            Map<String, Object> response = responseEntity.getBody();

            if (response == null)
                return Optional.empty();

            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
            if (results == null || results.isEmpty())
                return Optional.empty();

            Object voteAverage = results.get(0).get("vote_average");
            if (voteAverage == null)
                return Optional.empty();

            return Optional.of(((Number) voteAverage).doubleValue());
        } catch (Exception e) {
            log.warn("Erro ao buscar nota TMDB para '{}': {}", titulo, e.getMessage());
            return Optional.empty();
        }
    }
}