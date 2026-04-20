package br.com.blade.indicafilme.dto;

import br.com.blade.indicafilme.model.Plataform;
import br.com.blade.indicafilme.model.StatusFilme;
import jakarta.validation.constraints.*;

import java.util.List;

public class MovieRequestDto {
    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    @NotBlank(message = "O autor é obrigatório")
    private String autor;

   @NotNull(message = "O ano de lançamento é obrigatório")
   @Min(value = 1888, message = "O ano de lançamento deve ser a partir de 1888")
   @Max(value = 2100, message = "O ano de lançamento deve ser até 2100")
    private Integer anolancamento;

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

    private List<Plataform> plataformas;

    private StatusFilme status = StatusFilme.AGUARDANDO;

    // Getters e Setters


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

    public Integer getAnolancamento() {
        return anolancamento;
    }

    public void setAnolancamento(Integer anolancamento) {
        this.anolancamento = anolancamento;
    }

    public Integer getDuracao() {
        return duracao;
    }

    public void setDuracao(Integer duracao) {
        this.duracao = duracao;
    }

    public List<String> getGeneros() {
        return generos;
    }

    public void setGeneros(List<String> generos) {
        this.generos = generos;
    }
}