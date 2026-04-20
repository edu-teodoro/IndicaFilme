package br.com.blade.indicafilme.dto;

import br.com.blade.indicafilme.model.Movie;
import br.com.blade.indicafilme.model.Plataform;

import java.util.List;

/**
 * DTO (Data Transfer Object) para representar os dados de um filme na API.
 * 
 * <p>Em vez de expor a entidade {@link Movie} diretamente para o cliente,
 * usamos este objeto intermediário. Assim controlamos exatamente quais
 * campos o usuário pode ver - nunca campos internos como flags de status
 * que não são relevantes para o público.
 */

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
    private List<Plataform> plataformas;
    private String poster;

    // =============================================
    // Método estático de conversão: Movie -> MovieDto
    // Centraliza a lógica de formatação de notas num único lugar.
    // =============================================

    /**
     * Converte uma entidade {@link Movie} para um {@link MovieDto}, aplicando as regras de formatação de notas e calculando a média.
     * 
     * <p>Aplica a regra de formatação de notas:
     * nota {@literal >} 6 -> "N+" | nota {@literal <=} 6 -> "N" (parte inteira) | nota nula -> "_"</p>
     * 
     * @param movie entidade vinda do banco de dados.
     * @return DTO formatado para resposta da API.
     */

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

        // Aplica a regra de formatação em cada nota
        dto.notaDivina = formatarNota(movie.getNotaDivina());
        dto.notaPublico = formatarNota(movie.getNotaPublico());
        dto.mediaNotas = calcularMedia(movie.getNotaDivina(), movie.getNotaPublico());

        return dto;
    }

    /**
     * Formata uma nota numérica para exibição
     * 
     * <ul>
     *  <li>Decimal {@literal >=} 0.5 -> parte inteira + "+" (ex: 7.5 -> "7+")</li>
     *  <li>Decimal {@literal <} 0.5 -> somente parte inteira (ex: 6.3 -> "6")</li>
     * <li>Nota nula -> "_" (ex: null -> "_")</li>
     * </ul>
     * 
     * @param nota valor numérico da nota (pode ser null).
     * @return string formatada de acordo com as regras acima.
     */

    private static String formatarNota(Double nota){
        if (nota == null) return "_";
        int inteiro = (int) Math.floor(nota);
        return nota> 6.0 ? inteiro + "+" : String.valueOf(inteiro);
    }

    /**
     * Calcula a média entre nota divina e nota do público e já formata.
     * 
     * @param divina nota divina (pode ser null).
     * @param publico nota do público (pode ser null).
     * @return média formatada, ou nota disponível caso apenas uma exista, ou "_" se ambas forem nulas.
     */

    private static String calcularMedia(Double divina, Double publico) {
        if (divina == null && publico == null) return "_";
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
    public List<Plataform> getPlataformas() { return plataformas; }
    public String getPoster() { return poster; }
}