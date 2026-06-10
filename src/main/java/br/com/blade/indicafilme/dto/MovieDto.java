package br.com.blade.indicafilme.dto;

import br.com.blade.indicafilme.model.Movie;
import br.com.blade.indicafilme.model.Platform;
import lombok.Getter;
import java.util.List;

@Getter
public class MovieDto {

    private String id, titulo, autor, sinopse, notaDivina, notaPublico, mediaNotas, motivoRecomendacao, poster;
    private List<String> generos;
    private Integer duracao, anoLancamento;
    private List<Platform> plataformas;

    public static MovieDto fromMovie(Movie movie) {
        MovieDto dto = new MovieDto();
        dto.id = movie.getId();
        dto.titulo = movie.getTitulo();
        dto.autor = movie.getAutor();
        dto.generos = movie.getGeneros();
        dto.duracao = movie.getDuracao();
        dto.anoLancamento = movie.getAnoLancamento();
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
        if (nota == null) return "-";
        int inteiro = (int) Math.floor(nota);
        return (nota - inteiro) >= 0.5 ? inteiro + "+" : String.valueOf(inteiro);
    }

    static String calcularMedia(Double divina, Double publico) {
        if (divina == null && publico == null) return "-";
        if (divina == null) return formatarNota(publico);
        if (publico == null) return formatarNota(divina);
        return formatarNota((divina + publico) / 2.0);
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public List<String> getGeneros() { return generos; }
    public Integer getDuracao() { return duracao; }
    public Integer getAnoLancamento() { return anoLancamento; }
    public String getSinopse() { return sinopse; }
    public String getNotaDivina() { return notaDivina; }
    public String getNotaPublico() { return notaPublico; }
    public String getMediaNotas() { return mediaNotas; }
    public String getMotivoRecomendacao() { return motivoRecomendacao; }
    public List<Platform> getPlataformas() { return plataformas; }
    public String getPoster() { return poster; }
}