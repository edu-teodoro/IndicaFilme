package br.com.blade.indicafilme.model;

import org.springframework.data.annotation.PersistenceCreator;

/**
 * Representa uma plataforma de streaming onde o filme está disponivel.
 *
 * Usa {@code record} porque plataforma é um dado simples e imutável.
 * O {@code @PersistenceCreator} instrui o MongoDB a usar este construtor
 * ao ler os dados do banco.
 */
public record Plataform(
        String nome,
        String url
){
    /**
     * Construtor anotado para o MongoDB saber qual usar ao desserializar.
     *
     * @param nome nome da plataforma
     * @param url URL do filme na plataforma
     */
    @PersistenceCreator
    public Plataform{}
}