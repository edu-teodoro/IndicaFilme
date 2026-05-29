package br.com.blade.indicafilme.dto;

import br.com.blade.indicafilme.model.Movie;
import br.com.blade.indicafilme.model.Platform;

import java.util.List;

public class MovieDto {

    private String id;
    private String titulo;
    private String autor;
    private List<String> generos;
    private Integer duracao;
    private Integer anolancamento;
    private String sinopse;
    private String notaDivina;
    private String notaPublico;
    private String mediaNotas;
    private String motivoRecomendacao;
    private List<Platform> plataformas;
    private String poster;

    public static MovieDto fromMovie(Movie movie) {
        MovieDto dto = new MovieDto();
        dto.id = movie.getId();
        dto.titulo = movie.getTitulo();
        dto.autor = movie.getAutor();
        dto.generos = movie.getGeneros();
        dto.duracao = movie.getDuracao();
        dto.anolancamento = movie.getAnoLancamento();
        dto.sinopse = movie.getSinopse();
        dto.motivoRecomendacao = movie.getMotivoRecomendacao();
        dto.plataformas = movie.getPlataformas();
        dto.poster = movie.getPoster();
        dto.notaDivina = formatarNota(movie.getNotaDivina());
        dto.notaPublico = formatarNota(movie.getNotaPublico());
        dto.mediaNotas = calcularMedia(movie.getNotaDivina(), movie.getNotaPublico());
        return dto;
    }

    static String formatarNota(Double nota){
        if (nota == null) return "\u2014";
        int inteiro = (int) Math.floor(nota);
        double decimal = nota - inteiro;
        return nota>= 0.5 ? inteiro + "+" : String.valueOf(inteiro);
    }

    static String calcularMedia(Double divina, Double publico) {
        if (divina == null && publico == null) return "\u2014";
        if (divina == null) return formatarNota(publico);
        if (publico == null) return formatarNota(divina);
        return formatarNota((divina + publico) / 2.0);
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public List<String> getGeneros() { return generos; }
    public Integer getDuracao() { return duracao; }
    public Integer getAnolancamento() { return anolancamento; }
    public String getSinopse() { return sinopse; }
    public String getNotaDivina() { return notaDivina; }
    public String getNotaPublico() { return notaPublico; }
    public String getMediaNotas() { return mediaNotas; }
    public String getMotivoRecomendacao() { return motivoRecomendacao; }
    public List<Platform> getPlataformas() { return plataformas; }
    public String getPoster() { return poster; }
}