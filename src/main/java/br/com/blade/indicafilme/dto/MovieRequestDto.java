package br.com.blade.indicafilme.dto;

import br.com.blade.indicafilme.model.Platform;
import br.com.blade.indicafilme.model.StatusFilme;
import br.com.blade.indicafilme.validation.AnoMaximoAtual;
import jakarta.validation.constraints.*;

import java.util.List;

public class MovieRequestDto {

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    @NotBlank(message = "O nome do autor/diretor é obrigatório")
    private String autor;

    @NotNull(message = "O ano de lançamento é obrigatório")
    @Min(value = 1888, message = "O ano de lançamento deve ser a partir de 1888")
    @AnoMaximoAtual
    private Integer anoLancamento;

    @Positive(message = "A duração deve ser um valor positivo em minutos")
    private Integer duracao;

    private List<String> generos;

    @NotBlank(message = "A sinopse é obrigatória")
    private String sinopse;

    @DecimalMin(value = "0.0", message = "A nota divina deve ser no mínimo 0.0")
    @DecimalMax(value = "10.0", message = "A nota divina deve ser no máximo 10.0")
    private Double notaDivina;

    @DecimalMin(value = "0.0", message = "A nota do publico deve ser no mínimo 0.0")
    @DecimalMax(value = "10.0", message = "A nota do publico deve ser no máximo 10.0")
    private Double notaPublico;

    private String motivoRecomendacao;
    private String poster;
    private List<Platform> plataformas;
    private StatusFilme status = StatusFilme.AGUARDANDO;

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public Integer getAnoLancamento() { return anoLancamento; }
    public void setAnoLancamento(Integer anoLancamento) { this.anoLancamento = anoLancamento; }

    public Integer getDuracao() { return duracao; }
    public void setDuracao(Integer duracao) { this.duracao = duracao; }

    public List<String> getGeneros() { return generos; }
    public void setGeneros(List<String> generos) { this.generos = generos; }

    public String getSinopse() { return sinopse; }
    public void setSinopse(String sinopse) { this.sinopse = sinopse; }

    public Double getNotaDivina() { return notaDivina; }
    public void setNotaDivina(Double notaDivina) { this.notaDivina = notaDivina; }

    public Double getNotaPublico() { return notaPublico; }
    public void setNotaPublico(Double notaPublico) { this.notaPublico = notaPublico; }

    public String getMotivoRecomendacao() { return motivoRecomendacao; }
    public void setMotivoRecomendacao(String motivoRecomendacao) { this.motivoRecomendacao = motivoRecomendacao; }

    public String getPoster() { return poster; }
    public void setPoster(String poster) { this.poster = poster; }

    public List<Platform> getPlataformas() { return plataformas; }
    public void setPlataformas(List<Platform> plataformas) { this.plataformas = plataformas; }

    public StatusFilme getStatus() { return status; }
    public void setStatus(StatusFilme status) { this.status = status; }
}