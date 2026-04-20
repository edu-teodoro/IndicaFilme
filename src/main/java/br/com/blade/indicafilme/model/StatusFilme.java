package br.com.blade.indicafilme.model;

/**
 * Enum que representa o status de curadoria de um filme.
 *
 * {@code ATIVO}            - Filme aprovado e visível para os users.
 * {@code AGUARDANDO}       - Filme cadastrado mas ainda em revisão. Não aparece para users
 * {@code ERRO}             - Filme com problema no cadastro. Não aparece para users
 *
 */
public enum StatusFilme {
    ATIVO,
    AGUARDANDO,
    ERRO
}