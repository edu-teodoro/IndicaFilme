package br.com.blade.indicafilme.config;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

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

    private final Cache<String, AtomicInteger> rateLimitCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    public SecurityConfig(AdminProperties adminProperties) {
        this.adminProperties = adminProperties;
    }

    public void resetRateLimitCache() {
        rateLimitCache.invalidateAll();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .headers(headers -> {
                    headers.frameOptions(frame -> frame.deny());
                    headers.contentTypeOptions(ct -> {});
                    headers.referrerPolicy(rp -> rp.policy(
                            ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN));
                    headers.permissionsPolicy(pp -> pp.policy(
                            "camera=(), microphone=(), geolocation=(), payment=()"));
                    headers.contentSecurityPolicy(csp -> csp.policyDirectives(
                            "default-src 'self'; " +
                            "script-src 'self' 'unsafe-inline' https://www.googletagmanager.com https://www.google-analytics.com; " +
                            "style-src 'self https://fonts.googleapis.com https://cndjs.cloudflare.com 'unsafe-inline'; " +
                            "font-src 'self' https://fonts.gstatic.com https://cndjs.cloudflare.com data:; " +
                            "img-src 'self' https: data:; " +
                            "connect-src 'self' https://www.google-analytics.com https://www.googletagmanager.com; " +
                            "frame-ancestors 'none'"
                    ));
                })
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/admin/**").authenticated()
                        .requestMatchers("/actuator/health").permitAll()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(adminRateLimitFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(adminApiKeyFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> origins = new ArrayList<>(List.of("http://localhost:8080", "http://localhost:3000"));

        if (corsOrigins != null && !corsOrigins.isBlank()) {
            Arrays.stream(corsOrigins.split(",")).map(String::trim).filter(s -> !s.isEmpty()).forEach(origins::add);
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

    @Bean
    public OncePerRequestFilter adminRateLimitFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                if (request.getRequestURI().startsWith("/api/v1/admin/")) {
                    String ip = request.getRemoteAddr();
                    AtomicInteger counter = rateLimitCache.get(ip, k -> new AtomicInteger(0));
                    if (counter.incrementAndGet() > ADMIN_RATE_LIMIT) {
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

    @Bean
    public OncePerRequestFilter adminApiKeyFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                String path = request.getRequestURI();
                if (path.startsWith("/api/v1/admin/")) {
                    String key = request.getHeader(ADMIN_KEY_HEADER);
                    String expected = adminProperties.getApiKey();

                    if (expected == null || expected.isBlank() || !timingSafeEquals(expected, key)) {
                        log.warn("Tentativa de acesso admin não autorizado: path={}, ip={}", path, request.getRemoteAddr());
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json;charset=UTF-8");
                        Map<String, Object> body = new LinkedHashMap<>();
                        body.put("status", 401);
                        body.put("message", "Acesso não autorizado. Informe o header X-Admin-Key válido.");
                        body.put("path", path);
                        response.getWriter().write(objectMapper.writeValueAsString(body));
                        return;
                    }

                    SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken("admin", null,
                                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    private static boolean timingSafeEquals(String expected, String actual) {
        if (actual == null) return false;
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8));
    }
}