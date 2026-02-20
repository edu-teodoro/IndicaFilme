package com.example.blackflix.dto;

public class MovieSearchCriteria {
    private String genero;
    private String duracao;
    private Integer decada;

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getDuracao() {
        return duracao;
    }

    public void setDuracao(String duracao) {
        this.duracao = duracao;
    }

    public Integer getDecada() {
        return decada;
    }

    public void setDecada(Integer decada) {
        this.decada = decada;
    }
}
