package br.com.blade.indicafilme.dto;

import br.com.blade.indicafilme.model.Platform;
import br.com.blade.indicafilme.model.StatusFilme;
import br.com.blade.indicafilme.validation.AnoMaximoAtual;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
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

    private String motivoRecomendacao, poster;
    private List<Platform> plataformas;
    private StatusFilme status = StatusFilme.AGUARDANDO;
    
}