package br.com.blade.indicafilme.model;

import org.springframework.data.annotation.PersistenceCreator;

public record Plataform(
        String nome,
        String url
){
    @PersistenceCreator
    public Plataform{}
}