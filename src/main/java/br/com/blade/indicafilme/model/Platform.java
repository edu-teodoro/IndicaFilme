package br.com.blade.indicafilme.model;

import org.springframework.data.annotation.PersistenceCreator;

public record Platform(String nome, String url) {
    @PersistenceCreator
    public Platform {}
}