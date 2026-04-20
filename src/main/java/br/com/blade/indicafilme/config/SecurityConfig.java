package br.com.blade.indicafilme.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Configuração de segurança da aplicação Indica Filmes.
 * 
 * <p>Inclui: CORS dinâmico, headers de segurança (CSP, anti-clickjacking, MIME sniffing, referrer policy, permissions policy),
 * autenticação via API Key com comparação timing-safe, e rate limiting por IP nos endpoints admin.</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final String ADMIN_KEY_HEADER = "X-Admin-Key";

    private static final int ADMIN_RATE_LIMIT = 10;

    private final AdminProperties adminProperties;

    @Value("${cors.allowed-origins:}")
    private String corsOrigins;

    /** Cache de rate limit: IP -> contador de requisições. Expira em 1 minuto. */
    private final Cache<String, AtomicInteger> rateLimitCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    public SecurityConfig(AdminProperties adminProperties) {
        this.adminProperties = adminProperties;
    }

    /**
     * Limpa o cache de rate limit.
     * Usado nos testes para evitar que requisições acumuladas entre em test classes
     * causam falsos 429 (Too Many Requests).
     */
    public void resetRateLimitCache() {
        rateLimitCache.invalidateAll();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .headers(headers -> headers
                        .frameOptions(frame -> frame.deny())
                        .contentTypeOptions(ct -> {
                        })
                        .referrerPolicy(rp -> rp.policy(
                                ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; " +
                                        "script-src 'self'; " +
                                        "style-src 'self' https://fonts.googleapis.com 'unsafe-inline'; " +
                                        "font-src 'self' https://fonts.gstatic.com; " +
                                        "img-src 'self' https://image.tmdb.org data:; " +
                                        "connect-src 'self'; " +
                                        "frame-ancestors 'none';")))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/admin/**").authenticated()
                        .requestMatchers("/actuator/health").permitAll()
                        .anyRequest().permitAll())
                .addFilterBefore(adminRateLimitFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(adminApiKeyFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configuração de CORS - permite origens configuradas via variável de ambiente.
     * Em produção, defina CORS_ORIGINS=https://seu-dominio.com
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        List<String> origins = new ArrayList<>(List.of(
                "http://localhost:8080",
                "http://localhost:3000"));

        if (corsOrigins != null && !corsOrigins.isBlank()) {
            Arrays.stream(corsOrigins.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(origins::add);
            log.info("CORS: origens adicionais configurados: {}", corsOrigins);
        }

        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Rate limiting por IP para endpoints admin.
     * Máximo de {@value #ADMIN_RATE_LIMIT} requisições por minuto por IP.
     */
    @Bean
    public OncePerRequestFilter adminRateLimitFilter() {

        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, 
                                            HttpServletResponse response, 
                                            FilterChain filterChain)
                    throws ServletException, IOException {

                if (request.getRequestURI().startsWith("/api/v1/admin/")) {
                    String ip = request.getRemoteAddr();
                    AtomicInteger counter = rateLimitCache.get(ip, k -> new AtomicInteger(0));
                    int count = counter.incrementAndGet();

                    if (count > ADMIN_RATE_LIMIT) {
                        log.warn("Rate limit excedido: ip={}, count={}", ip, count);
                        response.setStatus(429);
                        response.setContentType("application/json;charset=UTF-8");
                        Map<String, Object> body = new LinkedHashMap<>();
                        body.put("status", 429);
                        body.put("message", "Muitas requisições. Tente novamente em 1 minuto.");
                        body.put("path", request.getRequestURI());
                        response.getWriter().write(objectMapper.writeValueAsString(body));
                        return;
                    }
                }

                filterChain.doFilter(request, response);
            }
        };
    }

    /**
     * Filtro que valida o header {@code X-Admin-Key} para rotas admin.
     * Usa comparação timing-safe para evitar ataques de timing side-channel.
     */
    @Bean
    public OncePerRequestFilter adminApiKeyFilter() {
        return new OncePerRequestFilter() {
        @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response, 
                                            FilterChain filterChain)
                    throws ServletException, IOException { 
                String path = request.getRequestURI();

                if (path.startsWith("/api/v1/admin/")) {
                    String key = request.getHeader(ADMIN_KEY_HEADER);
                    String expected = adminProperties.getApiKey();

                    if (expected == null || expected.isBlank()|| !timingSafeEquals(expected, key)) {
                        log.warn("Tentativa de acesso admin não autorizado: path={}, ip={}", 
                                path, request.getRemoteAddr());
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json;charset=UTF-8");
                        Map<String, Object> body = new LinkedHashMap<>();
                        body.put("status", 401);
                        body.put("message", "Acesso não autorizado. Informe o header X-Admin-Key válido.");
                        body.put("path", path);
                        response.getWriter().write(objectMapper.writeValueAsString(body));
                        return;
                    }
                    
                    log.debug("Acesso admin autorizado: path={}, ip={}", path);
                    SecurityContextHolder.getContext()
                    .setAuthentication(new UsernamePasswordAuthenticationToken(
                        "admin", null, 
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                        ));
                    }

                filterChain.doFilter(request, response);
                }
            };
        }

    /**
     * Comparação timing-safe de duas strings.
     * Evita ataques de timing que tentam adivinhar a API Key caractere por caractere.
     */
    private static boolean timingSafeEquals(String expected, String actual) {
        if (actual == null) return false;
        return MessageDigest.isEqual(
            expected.getBytes(StandardCharsets.UTF_8), 
            actual.getBytes(StandardCharsets.UTF_8)
        );
    }
}