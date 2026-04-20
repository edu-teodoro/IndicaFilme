package br.com.blade.indicafilme.repository;

import br.com.blade.indicafilme.model.Movie;
import br.com.blade.indicafilme.model.Plataform;
import br.com.blade.indicafilme.model.StatusFilme;

import java.util.List;

public class FilmeData {

    public static List<Movie> todos() {
        return List.of(
            criarFilme(
                "1",
                "Um sonho de liberdade",
                "Frank Darabont",
                List.of("Drama", "Crime"),
                142,
                1994,
                "Dois homens presos formam uma amizade duradoura, encontrando consolo e redenção através de atos de decência comum.",
                8.2,
                9.3,
                "Um filme emocionante e inspirador que mostra a força da amizade e da esperança mesmo nas circunstâncias mais difíceis.",
                "https://image.tmdb.org/t/p/w500/q6y0Go1tsGEsmtFryDOJo3dEmqu.jpg",
                List.of(
                    new Plataform("Netflix", "https://www.netflix.com/title/70005379"),
                    new Plataform("HBO Max", "https://www.hbomax.com/br/pt/movies/um-sonho-de-liberdade/9b4dacba-2f80-4272-aac7-bb5e2ae91343?utm_source=universal_search")
                )
                ),
                criarFilme(
                    "2",
                    "O Poderoso Chefão",
                    "Francis Ford Coppola",
                    List.of("Drama", "Crime"),
                    175,
                    1972,
                    "O patriarca idoso de uma dinastia do crime organizado transfere o controle de seu império clandestino para seu relutante filho mais novo.",
                    7.8,
                    9.2,
                    "Um clássico do cinema que explora temas de poder, família e corrupção, com atuações memoráveis e uma narrativa envolvente.",
                    "https://image.tmdb.org/t/p/w500/3bhkrj58Vtu7enYsRolD1fZdja1.jpg",
                    List.of(
                        new Plataform("Amazon Prime Video", "https://www.primevideo.com/detail/0GUKG9C1Q5KJZP2XQYB8YVZP7"),
                        new Plataform("HBO Max", "https://www.hbomax.com/br/pt/movies/o-poderoso-chefao/1c86e9d8-6c9b-4e5a-9b3a-1c3e7b8e5f2d?utm_source=universal_search")
                    )
                ),
                criarFilme(
                    "3",
                    "Paprika",
                    "Satoshi Kon",
                    List.of("Animação", "Ficção Científica"),
                    104,
                    2006,
                    "Uma psicóloga que trabalha com um dispositivo que permite entrar nos sonhos dos pacientes tem sua vida virada de cabeça para baixo quando o dispositivo é roubado e usado para causar caos.",
                    10.0,
                    7.7,
                    "Uma obra-prima da animação japonesa que mistura elementos de ficção científica e surrealismo, explorando temas de identidade, realidade e o poder dos sonhos de maneira visualmente deslumbrante e narrativamente complexa.",
                    "https://m.media-amazon.com/images/S/pv-target-images/7863540cdfeb19c162bb351209a5c5c13505f7354f789578c504740000f0e4ad.jpg",
                    List.of(
                        new Plataform("Amazon Prime Video", "https://www.primevideo.com/-/pt/detail/Paprika/0NYGDOLKTHERB0EOCO22SLKMW8"),
                        new Plataform("Apple TV", "https://tv.apple.com/br/movie/paprika/umc.cmc.6i2m8ev8mr7b498w9orw4kiht?action=play")
                    )
                ),
                criarFilme(
                    "4",
                    "A Viagem de Chihiro",
                    "Hayao Miyazaki",
                    List.of("Animação", "Aventura"),
                    125,
                    2001,
                    "Uma jovem garota chamada Chihiro se encontra em um mundo mágico e misterioso, onde deve encontrar uma maneira de salvar seus pais e retornar ao mundo real.",
                    10.0,
                    8.6,
                    "Uma obra-prima da animação japonesa que combina uma narrativa encantadora, personagens memoráveis e uma estética visual deslumbrante para criar uma experiência cinematográfica única e inesquecível.",
                    "https://m.media-amazon.com/images/M/MV5BYmZmMmM4OTYtMDkyNi00ZDI5LThiODItNzhlZGI3ZDJmZDZiXkEyXkFqcGc@._V1_FMjpg_UX1000_.jpg",
                    List.of(
                        new Plataform("Netflix", "https://www.netflix.com/title/70036457"),
                        new Plataform("Amazon", "https://www.amazon.com.br/Viagem-Chihiro-Hayao-Miyazaki/dp/B00B9X7Z5C")
                    )
                ),
                criarFilme(
                    "5",
                    "Cidade de Deus",
                    "Fernando Meirelles",
                    List.of("Drama", "Crime"),
                    130,
                    2002,
                    "Dois meninos crescem em uma favela do Rio de Janeiro, mas seguem caminhos diferentes - um se torna um fotógrafo, enquanto o outro se torna um traficante de drogas.",
                    8.0,
                    8.6,
                    "Um filme impactante e visceral que oferece uma visão crua e realista da vida nas favelas do Rio de Janeiro, com uma narrativa envolvente e atuações poderosas.",
                    "https://image.tmdb.org/t/p/w500/4GqjLhYjzZtXyZt3iQb1u9nq7A.jpg",
                    List.of(
                        new Plataform("Amazon Prime Video", "https://www.primevideo.com/detail/0GUKG9C1Q5KJZP2XQYB8YVZP7"),
                        new Plataform("HBO Max", "https://www.hbomax.com/br/pt/movies/cidade-de-deus/1c86e9d8-6c9b-4e5a-9b3a-1c3e7b8e5f2d?utm_source=universal_search")
                    )
            ),
            criarFilme(
                "6",
                "O Labirinto do Fauno",
                "Guillermo del Toro",
                List.of("Fantasia", "Guerra"),
                118,
                2006,
                "Durante a Guerra Civil Espanhola, uma jovem garota chamada Ofelia descobre um mundo mágico e perigoso, onde deve completar três tarefas para provar que é a reencarnação de uma princesa perdida.",
                9.0,
                8.2,
                "Um filme visualmente deslumbrante e narrativamente complexo que mistura elementos de fantasia e realidade para criar uma experiência cinematográfica única e inesquecível, explorando temas de inocência, coragem e resistência em meio à guerra.",
                "https://image.tmdb.org/t/p/w500/6aUWe0GSl69wMTSWWexS3qSsaL.jpg",
                List.of(
                    new Plataform("Amazon Prime Video", "https://www.primevideo.com/detail/0GUKG9C1Q5KJZP2XQYB8YVZP7"),
                    new Plataform("HBO Max", "https://www.hbomax.com/br/pt/movies/o-labirinto-do-fauno/1c86e9d8-6c9b-4e5a-9b3a-1c3e7b8e5f2d?utm_source=universal_search")
                )
            ),
            criarFilme(
                "7",
                "As Cronicas de Spiderwick",
                "Mark Waters",
                List.of("Fantasia", "Aventura"),
                112,
                2008,
                "Uma jovem garota descobre um mundo mágico e perigoso, onde deve completar três tarefas para provar que é a reencarnação de uma princesa perdida.",
                8.0,
                7.8,
                "Um filme envolvente e visualmente impressionante que combina elementos de fantasia e aventura para criar uma experiência cinematográfica única e inesquecível.",
                "https://image.tmdb.org/t/p/w500/6aUWe0GSl69wMTSWWexS3qSsaL.jpg",
                List.of(
                    new Plataform("Amazon Prime Video", "https://www.primevideo.com/detail/0GUKG9C1Q5KJZP2XQYB8YVZP7"),
                    new Plataform("HBO Max", "https://www.hbomax.com/br/pt/movies/as-cronicas-de-spiderwick/1c86e9d8-6c9b-4e5a-9b3a-1c3e7b8e5f2d?utm_source=universal_search")
                )
            )
        );
    }

    /* Método auxiliar privado para construir um Movie de forma legível */

    private static Movie criarFilme(
            String id, String titulo, String autor,
            List<String> generos, Integer duracao, Integer anoLancamento,
            String sinopse, Double notaDivina, Double notaPublico,
            String motivoRecomendacao, String poster, List<Plataform> plataformas) {

        Movie m = new Movie();
        m.setId(id);
        m.setTitulo(titulo);
        m.setAutor(autor);
        m.setStatus(StatusFilme.ATIVO);
        m.setGeneros(generos);
        m.setDuracao(duracao);
        m.setAnoLancamento(anoLancamento);
        m.setSinopse(sinopse);
        m.setNotaDivina(notaDivina);
        m.setNotaPublico(notaPublico);
        m.setMotivoRecomendacao(motivoRecomendacao);
        m.setPoster(poster);
        m.setPlataformas(plataformas);
        return m;
    }
}
