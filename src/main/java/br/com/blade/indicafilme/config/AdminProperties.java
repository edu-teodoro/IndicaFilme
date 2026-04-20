package br.com.blade.indicafilme.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "admin")
public class AdminProperties {
    private static final Logger log = LoggerFactory.getLogger(AdminProperties.class);

    private String apiKey;

    @PostConstruct
    void verificarApiKey() {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("===============================================================");
            log.warn(" ADMIN_API_KEY não está configurada! ");
            log.warn( " Endpoints admin (/api/v1/admin/**) estarão BLOQUEADOS.");
            log.warn(" Defina a variável de ambiente ADMIN_API_KEY para habilitá-los.");
            log.warn("===============================================================");
        }

    }


    // =========== Getters and Setters ===========
    public String getApiKey() {
        return apiKey;}

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;}

    }
