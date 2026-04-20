package br.com.blade.indicafilme.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

/**
 * DTO para atualização parcial de campos "divinos" de um filme.
 *
 * usado no endpoint {@code PATCH /api/v1/admin/movies/{id}/divine}
 * para atualizar apenas os campos que o administrador controla:
 * {@code notaDivina} e {@code motivoRecomendacao}.
 *
 * Todos os campos são opcionais - {@code null} significa "não alterar".
 */
public class DivinePatchDto{

    /**
     * Nova nota divina (do adm). Deve estar entre 0.0 e 10.0.
     * {@code null} = não alterar.
     */
    @DecimalMin(value = "0.0", message = "A nota divina deve ser no minimo 0")
    @DecimalMax(value = "10.0", message = "A nota divina deve ser no maximo 10")
    private Double notaDivina;

    /**
     * Novo texto explicando por que o admin recomenda este filme.
     * {@code null} = não alterar.
     */
    private String motivoRecomendacao;

    // ========================= Getters e Setters =========================

    /** @return nova nota divina, ou {@code null} se não deve ser alterada. */
    public Double getNotaDivina() { return notaDivina; }

    /** @param notaDivina nova nota divina. */
    public void setNotaDivina(Double notaDivina) { this.notaDivina = notaDivina; }

    /** @return novo motivo de recomendação, ou {@code null} se não deve ser alterado. */
    public String getMotivoRecomendacao() { return motivoRecomendacao; }

    /** @param motivoRecomendacao novo motivo de recomendação. */
    public void setMotivoRecomendacao(String motivoRecomendacao) { this.motivoRecomendacao = motivoRecomendacao; }
}
