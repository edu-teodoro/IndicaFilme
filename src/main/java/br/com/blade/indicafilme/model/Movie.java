package br.com.blade.indicafilme.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "movies")
public class Movie {

    @Id
    private String id;
    private String titulo, autor, sinopse, motivoRecomendacao, poster;
    private StatusFilme status = StatusFilme.AGUARDANDO;
    private List<String> generos;
    private Integer duracao, anoLancamento;
    private Double notaDivina, notaPublico;
    private List<Platform> plataformas;

}