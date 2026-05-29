package br.com.blade.indicafilme.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public class DivinePatchDto{

    @DecimalMin(value = "0.0", message = "A nota divina deve ser no minimo 0.0")
    @DecimalMax(value = "10.0", message = "A nota divina deve ser no maximo 10.0")
    private Double notaDivina;

    private String motivoRecomendacao;

    public Double getNotaDivina() { return notaDivina; }
    public void setNotaDivina(Double notaDivina) { this.notaDivina = notaDivina; }

    public String getMotivoRecomendacao() { return motivoRecomendacao; }
    public void setMotivoRecomendacao(String motivoRecomendacao) { this.motivoRecomendacao = motivoRecomendacao; }
}
