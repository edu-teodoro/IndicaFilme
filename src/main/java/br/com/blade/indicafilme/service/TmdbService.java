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

/**
 * Serviço de integração com a API do TMDB (The Movie Database).
 *
 * Responsável por buscar a nota do público ({@code vote_avarage})
 * de uma obra a partir do título e ano de lançamento.
 *
 * Resultados são cacheados por 1 hora (Caffeine) para evitar
 * chamadas repetidas à API do TMDB para o mesmo filme.
 */
@Service
public class TmdbService {

    private static final Logger log = LoggerFactory.getLogger(TmdbService.class);

    private final TmdbProperties tmdbProperties;
    private final RestTemplate restTemplate;

    /** Cache de notas do TMDB: chave "titulo_ano" -> Optional<nota>. TTL de 1h. */
    private final Cache<String, Optional<Double>> tmdbCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .maximumSize(500)
            .build();

    /**
     * Construtor com injeção de dependências.
     *
     * @param tmdbProperties propriedades de configuração do TMDB.
     * @param restTemplate cliente HTTP do Spring para chamadas REST.
     */
    public TmdbService(TmdbProperties tmdbProperties, RestTemplate restTemplate) {
        this.tmdbProperties = tmdbProperties;
        this.restTemplate = restTemplate;
    }

    /**
     *
     * Busca a nota do público no TMDB para um filme específico.
     * Resultados são cacheados por 1 hora para evitar chamadas repetidas.
     *
     * @param titulo                título do filme (ex: "Interestelar").
     * @param anoLancamento         ano de lançamento do filme (ex: 2014). Pode ser nulo.
     * @return {@link Optional}     com a nota (0.0 a 10.0), ou vazio se não encontrado/erro.
     */
    public Optional<Double> buscarNotaPublico(String titulo, Integer anoLancamento) {
        String apiKey = tmdbProperties.getApiKey();

        if (apiKey == null || apiKey.isBlank()){
            log.debug("TMDB API Key não configurada - nota do público não será buscada.");
            return Optional.empty();
        }

        String cacheKey = titulo + "_" + anoLancamento;
        return tmdbCache.get(cacheKey, k -> buscarNotaPublicoInterno(titulo, anoLancamento));
    }

    /**
     * Realiza a chamada HTTP real ao TMDB. Chamado apenas em cache miss.
     */
    private Optional<Double> buscarNotaPublicoInterno(String titulo, Integer anoLancamento) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder
            .fromHttpUrl(tmdbProperties.getBaseUrl() + "/search/movies")
            .queryParam("query", titulo)
            .queryParam("language", "pt-BR");

            if (anoLancamento != null) {
                builder.queryParam("year", anoLancamento);                
            }

            String url = builder.build().toUriString();
            log.debug("Buscando nota TMDB para '{}' ({})", titulo, anoLancamento);

            // Usa Bearer token no header em vez de api_key na query param (mais seguro)
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(tmdbProperties.getApiKey());
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            @SuppressWarnings("uncheked")
                    ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> response = responseEntity.getBody();

            if (response == null) {
                log.warn("TMDB retornou resposta nula para '{}'", titulo);
                return Optional.empty();
            }

            @SuppressWarnings("uncheked")
                    List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

            if (results == null || results.isEmpty()) {
                log.info("TMDB: nenhum resultado para '{}' ({})", titulo, anoLancamento);
                return Optional.empty();
            }

            Object voteAverage = results.get(0).get("vote_average");
            if (voteAverage == null) {
                return Optional.empty();
            }

            double nota = ((Number) voteAverage).doubleValue();
            log.info("TMDB: nota do público para '{}' = {}",titulo, nota);
            return Optional.of(nota);

        } catch (Exception e) {
            log.warn("Erro ao buscar nota TMDB para '{}': {}", titulo, e.getMessage());
            return Optional.empty();
        }
    }
}