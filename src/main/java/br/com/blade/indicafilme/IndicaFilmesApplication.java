package br.com.blade.indicafilme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação Indica Filmes.
 * Responsavel por inicializar o contexto do Spring Boot.
 */
@SpringBootApplication
public class IndicaFilmesApplication {
    public static void main(String[] args) {
        SpringApplication.run(IndicaFilmesApplication.class, args);
    }
}