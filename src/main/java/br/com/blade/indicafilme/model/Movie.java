package br.com.blade.indicafilme.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "movies")
public class Movie {

    @Id
    private String id;
    private String titulo;
    private String autor;
    private StatusFilme status = StatusFilme.AGUARDANDO;
    private List<String> generos;
    private Integer duracao;
    private Integer anoLancamento;
    private String sinopse;
    private Double notaDivina;
    private Double notaPublico;
    private String motivoRecomendacao;
    private List<Platform> plataformas;
    private String poster;

    public Movie() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public StatusFilme getStatus() { return status; }
    public void setStatus(StatusFilme status) { this.status = status; }

    public List<String> getGeneros() { return generos; }
    public void setGeneros(List<String> generos) { this.generos = generos; }

    public Integer getDuracao() { return duracao; }
    public void setDuracao(Integer duracao) { this.duracao = duracao; }

    public Integer getAnoLancamento() { return anoLancamento; }
    public void setAnoLancamento(Integer anoLancamento) { this.anoLancamento = anoLancamento; }

    public String getSinopse() { return sinopse; }
    public void setSinopse(String sinopse) { this.sinopse = sinopse; }

    public Double getNotaDivina() { return notaDivina; }
    public void setNotaDivina(Double notaDivina) { this.notaDivina = notaDivina; }

    public Double getNotaPublico() { return notaPublico; }
    public void setNotaPublico(Double notaPublico) { this.notaPublico = notaPublico; }

    public String getMotivoRecomendacao() { return motivoRecomendacao; }
    public void setMotivoRecomendacao(String motivoRecomendacao) { this.motivoRecomendacao = motivoRecomendacao; }

    public List<Platform> getPlataformas() { return plataformas; }
    public void setPlataformas(List<Platform> plataformas) { this.plataformas = plataformas; }

    public String getPoster() { return poster; }
    public void setPoster(String poster) { this.poster = poster; }
}