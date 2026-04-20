package br.com.blade.indicafilme.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

/**
 * Representa um filme no sistema Indica Filmes.
 *
 * Persistido na coleção {@code movies} do MongoDB.
 *
 * Campos "divinos" ({@code notaDivina}, {@code motivoRecomendacao}) são
 * definidos por você, o administrador, diretamente no banco via MongoDB Compass.
 * Os demais são dados públicos do fillme.
 */
@Document(collection = "movies")
public class Movie {

    /**
     * Identificador único gerado automaticamente pelo MongoDB.
     * {@code @Id} instrui o Spring Data a usar este campo como chave primária
     */
    @Id
    private String id;
    private String titulo;
    private String autor;
    /**
     * Status de curadoria.
     * Apenas filmes {@link StatusFilme#ATIVO} aparecem para os usuários
     */
    private StatusFilme status = StatusFilme.AGUARDANDO;
    private List<String> generos;
    private Integer duracao;
    private Integer anoLancamento;
    private String sinopse;
    private Double notaDivina;
    private Double notaPublico;
    private String motivoRecomendacao;
    private List<Plataform> plataformas;
    private String poster;

    public Movie() {}

    // === Getters e Setters ===

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public StatusFilme getStatus() {
        return status;
    }

    public void setStatus(StatusFilme status) {
        this.status = status;
    }

    public List<String> getGeneros() {
        return generos;
    }

    public void setGeneros(List<String> generos) {
        this.generos = generos;
    }

    public Integer getDuracao() {
        return duracao;
    }

    public void setDuracao(Integer duracao) {
        this.duracao = duracao;
    }

    public Integer getAnoLancamento() {
        return anoLancamento;
    }

    public void setAnoLancamento(Integer anoLancamento) {
        this.anoLancamento = anoLancamento;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public Double getNotaDivina() {
        return notaDivina;
    }

    public void setNotaDivina(Double notaDivina) {
        this.notaDivina = notaDivina;
    }

    public Double getNotaPublico() {
        return notaPublico;
    }

    public void setNotaPublico(Double notaPublico) {
        this.notaPublico = notaPublico;
    }

    public String getMotivoRecomendacao() {
        return motivoRecomendacao;
    }

    public void setMotivoRecomendacao(String motivoRecomendacao) {
        this.motivoRecomendacao = motivoRecomendacao;
    }

    public List<Plataform> getPlataformas() {
        return plataformas;
    }

    public void setPlataformas(List<Plataform> plataformas) {
        this.plataformas = plataformas;
    }
    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

}