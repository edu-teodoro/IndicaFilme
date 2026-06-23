package br.com.blade.indicafilme.repository;

import br.com.blade.indicafilme.model.Movie;
import br.com.blade.indicafilme.model.Platform;
import br.com.blade.indicafilme.model.StatusFilme;

import java.util.List;

public class FilmeData {

        public static List<Movie> todos() {
                return List.of(
                                criarFilme("1", "Senhor dos Anéis: A Comunidade do Anel", "Peter Jackson",
                                                List.of("Fantasia", "Aventura", "Drama", "abc"), 208, 2001,
                                                "Um hobbit despretensiosos encontra um anel invisível e aprende que deve lutar contra o poderoso senhor dos anéis.",
                                                9.0, 8.8,
                                                "Uma épica visualmente deslumbrante que adapta brilhantemente um clássico da literatura, com uma narrativa envolvente e efeitos especiais revolucionários.",
                                                "https://br.web.img3.acsta.net/medias/nmedia/18/92/91/32/20224832.jpg",
                                                List.of(
                                                                new Platform("Amazon Prime Video",
                                                                                "https://www.primevideo.com/dp/amzn1.dv.gti.84a9f6d5-bc3f-73a3-e41f-a82b4ff83e67?autoplay=0&ref_=atv_cf_strg_wb"),
                                                                new Platform("Netflix",
                                                                                "https://www.netflix.com/br/title/60004480?source=35&fromWatch=true"),
                                                                new Platform("HBO Max",
                                                                                "https://www.hbomax.com/br/pt/movies/o-senhor-dos-aneis-i-a-sociedade-do-anel-versao-estendida/cc028130-b24d-48b1-97ad-78cc38011625?utm_source=universal_search"))),
                                criarFilme("2", "Interestelar", "Christopher Nolan",
                                                List.of("Ficção Científica", "Drama", "Aventura"), 169, 2014,
                                                "Um grupo de astronautas viaja através de um buraco de minhoca perto de Júpiter para encontrar um novo lar para a humanidade.",
                                                8.6, 8.6,
                                                "Uma obra-prima de ficção científica que combina visão ambiciosa com emoção humana, apresentando conceitos complexos de forma acessível.",
                                                "https://acdn-us.mitiendanube.com/stores/004/687/740/products/pos-01876-4c8ebd420e08f8359717181254801917-1024-1024.webp",
                                                List.of(
                                                                new Platform("HBO Max",
                                                                                "https://www.hbomax.com/br/pt/movies/interestelar/aa5b9295-8f9c-44f5-809b-3f2b84badfbf?utm_source=universal_search"),
                                                                new Platform("Amazon Prime Video",
                                                                                "https://www.primevideo.com/dp/amzn1.dv.gti.b4a9f7c6-5def-7e63-9aa7-df38a479333e?autoplay=0&ref_=atv_cf_strg_wb"))),
                                criarFilme("3", "Parasita", "Bong Joon-ho", List.of("Drama", "Thriller"), 132, 2019,
                                                "A família Kim, de baixa renda, planeja uma operação para se infiltrar na residência de uma família rica.",
                                                8.6, 8.5,
                                                "Um filme brilhantemente executado que mistura humor, drama e comentário social, com uma narrativa que mantém você na beira do assento.",
                                                "https://image.tmdb.org/t/p/w600_and_h900_bestv2/7IiTTgloJzvGI1TAYymCfbfl3vT.jpg",
                                                List.of(
                                                                new Platform("Netflix",
                                                                                "https://www.netflix.com/br/title/81221938?source=35&fromWatch=true"),
                                                                new Platform("Amazon Prime Video",
                                                                                "https://www.primevideo.com/dp/amzn1.dv.gti.8721e1af-4a30-472b-93b2-95134fb5c2d8?autoplay=0&ref_=atv_cf_strg_wb"))),
                                criarFilme("4", "De Volta para o Futuro", "Robert Zemeckis",
                                                List.of("Ficção Científica", "Comédia", "Aventura"), 116, 1985,
                                                "Um adolescente é acidentalmente enviado ao passado em uma máquina do tempo e deve garantir que seus pais se apaixonem para que ele exista.",
                                                8.5, 8.5,
                                                "Um clássico atemporal que combina humor, ação e uma história de viagem no tempo perfeitamente executada, com personagens memoráveis.",
                                                "https://m.media-amazon.com/images/M/MV5BZDcyNmYxN2QtMmViZS00NTQ4LTlhZTAtZjc4MzY2Yjg0M2ZmXkEyXkFqcGc@._V1_.jpg",
                                                List.of(
                                                                new Platform("Amazon Prime Video",
                                                                                "https://www.primevideo.com/dp/amzn1.dv.gti.30a9f73b-becf-e11e-b945-01fc05bd6430?autoplay=0&ref_=atv_cf_strg_wb"),
                                                                new Platform("HBO Max",
                                                                                "https://www.hbomax.com/br/pt/movies/de-volta-para-o-futuro/d60daa51-9799-4b5f-8d1a-f29cea278788?utm_source=universal_search"))),
                                criarFilme("5", "Clube da Luta", "David Fincher", List.of("Drama", "Thriller"), 139,
                                                1999,
                                                "Um insomne contador conhece um vendedor de sabão carismático, e ambos formam um clube secreto de luta que evolui em algo muito mais sombrio.",
                                                8.8, 8.8,
                                                "Um thriller psicológico visceral e inovador que permanece relevante e impactante, com uma reviravolta final memorável.",
                                                "https://m.media-amazon.com/images/I/61vKJHwfCUL._AC_UF894,1000_QL80_.jpg",
                                                List.of(
                                                                new Platform("Netflix",
                                                                                "https://www.netflix.com/br/title/26004747?source=35&fromWatch=true"),
                                                                new Platform("Amazon Prime Video",
                                                                                "https://www.primevideo.com/dp/amzn1.dv.gti.84aa074e-d217-477f-9242-3e62c961c14b?autoplay=0&ref_=atv_cf_strg_wb"))),
                                criarFilme("6", "Whiplash", "Damien Chazelle", List.of("Drama", "Música"), 107, 2014,
                                                "Um baterista ambicioso num colégio de artes recebe a atenção de um regente abusivo que o coloca em um jogo perturbador de desempenho.",
                                                8.5, 8.5,
                                                "Um drama intenso e angustiante sobre obsessão e excelência, com cenas de músicas incríveis e uma tensão praticamente insuportável.",
                                                "https://rollingstone.com.br/wp-content/uploads/legacy/2014/img-1026474-whiplash-poster.jpg",
                                                List.of(
                                                                new Platform("HBO Max",
                                                                                "https://www.hbomax.com/br/pt/movies/whiplash-em-busca-da-perfeicao/ede72271-f953-4416-a3a4-b501e704befc?utm_source=universal_search"),
                                                                new Platform("Amazon Prime Video",
                                                                                "https://www.primevideo.com/dp/amzn1.dv.gti.7ca9f6c6-4216-7934-b5d9-4217eaa63151?autoplay=0&ref_=atv_cf_strg_wb"))),
                                criarFilme("7", "Oldboy", "Park Chan-wook", List.of("Ação", "Thriller", "Crime"), 120,
                                                2003,
                                                "Um homem é preso em um quarto anônimo por 15 anos, depois é libertado e deve descobrir por que foi sequestrado e quem foi o responsável.",
                                                8.4, 8.4,
                                                "Um thriller de vingança visualmente impressionante com uma reviravolta perturbadora, abordando temas de justiça e redenção.",
                                                "https://image.tmdb.org/t/p/original/pWDtjs568ZfOTMbURQBYuT4Qxka.jpg",
                                                List.of(
                                                                new Platform("Amazon Prime Video",
                                                                                "https://www.primevideo.com/dp/amzn1.dv.gti.595e6db6-b814-4f6c-8afe-90da57887247?autoplay=0&ref_=atv_cf_strg_wb"))),
                                criarFilme("8", "A Viagem de Chihiro", "Hayao Miyazaki",
                                                List.of("Animação", "Aventura"), 125, 2001,
                                                "Uma jovem garota chamada Chihiro se encontra em um mundo mágico e misterioso, onde deve encontrar uma maneira de salvar seus pais e retornar ao mundo real.",
                                                10.0, 8.6,
                                                "Uma obra-prima da animação japonesa que combina uma narrativa encantadora, personagens memoráveis e uma estética visual deslumbrante para criar uma experiência cinematográfica única e inesquecível.",
                                                "https://m.media-amazon.com/images/M/MV5BYmZmMmM4OTYtMDkyNi00ZDI5LThiODItNzhlZGI3ZDJmZDZiXkEyXkFqcGc@._V1_FMjpg_UX1000_.jpg",
                                                List.of(new Platform("Netflix",
                                                                "https://www.netflix.com/title/70036457"),
                                                                new Platform("Amazon",
                                                                                "https://www.amazon.com.br/Viagem-Chihiro-Hayao-Miyazaki/dp/B00B9X7Z5C"))),
                                criarFilme("9", "Seven", "David Fincher", List.of("Thriller", "Crime", "Drama"), 127,
                                                1995,
                                                "Dois detetives enfrentam um serial killer que usa os sete pecados capitais como tema para seus assassinatos brutais.",
                                                8.6, 8.6,
                                                "Um thriller sombrio e perturbador que estabeleceu o tom para muitos filmes de crime posteriores, com uma atmosfera opressiva e uma reviravolta devastadora.",
                                                "https://image.tmdb.org/t/p/w500/zgB9sNxR5G43qyI9KZjCv8smpO8.jpg",
                                                List.of(new Platform("Netflix",
                                                                "https://www.netflix.com/title/70022152"),
                                                                new Platform("HBO Max",
                                                                                "https://www.hbomax.com/br/pt/movies/seven/"))),
                                criarFilme("10", "As Crônicas de Spiderwick", "Mark Waters",
                                                List.of("Fantasia", "Aventura", "Comédia"), 112, 2008,
                                                "Uma família se vê envolvida em um mundo de criaturas mágicas ocultas quando descobrem um manual de criaturas fantásticas.",
                                                8.0, 7.8,
                                                "Um filme envolvente e visualmente impressionante que combina elementos de fantasia e aventura para criar uma experiência cinematográfica única e inesquecível.",
                                                "https://image.tmdb.org/t/p/w500/fP2lGsqVwPpgHqqd4huwKNdg2Tz.jpg",
                                                List.of(new Platform("Amazon Prime Video",
                                                                "https://www.primevideo.com/detail/0GUKG9C1Q5KJZP2XQYB8YVZP7"),
                                                                new Platform("HBO Max",
                                                                                "https://www.hbomax.com/br/pt/movies/as-cronicas-de-spiderwick/"))),
                                criarFilme("11", "O Pálido Olho Azul", "S. Darko", List.of("Horror", "Drama"), 90, 2007,
                                                "Uma exploradora de cavernas descobre uma criatura antiga que coloca em risco a sua vida e a de sua equipe.",
                                                6.5, 6.2,
                                                "Um filme de horror atmosférico que cria tensão através de isolamento e o desconhecido.",
                                                "https://image.tmdb.org/t/p/w500/nAU4dVvkZgQvR0M7tKAv9qgtcGL.jpg",
                                                List.of(new Platform("Amazon Prime Video",
                                                                "https://www.primevideo.com/detail/0I6KLMNOQPQRQ2XQYB8YVZP9Z"),
                                                                new Platform("Apple TV",
                                                                                "https://tv.apple.com/br/movie/o-palido-olho-azul/"))),
                                criarFilme("12", "Paprika", "Satoshi Kon", List.of("Animação", "Ficção Científica"),
                                                104, 2006,
                                                "Uma psicóloga que trabalha com um dispositivo que permite entrar nos sonhos dos pacientes tem sua vida virada de cabeça para baixo quando o dispositivo é roubado e usado para causar caos.",
                                                10.0, 7.7,
                                                "Uma obra-prima da animação japonesa que mistura elementos de ficção científica e surrealismo, explorando temas de identidade, realidade e o poder dos sonhos de maneira visualmente deslumbrante e narrativamente complexa.",
                                                "https://m.media-amazon.com/images/S/pv-target-images/7863540cdfeb19c162bb351209a5c5c13505f7354f789578c504740000f0e4ad.jpg",
                                                List.of(new Platform("Amazon Prime Video",
                                                                "https://www.primevideo.com/-/pt/detail/Paprika/0NYGDOLKTHERB0EOCO22SLKMW8"),
                                                                new Platform("Apple TV",
                                                                                "https://tv.apple.com/br/movie/paprika/umc.cmc.6i2m8ev8mr7b498w9orw4kiht?action=play"))),
                                criarFilme("13", "Castelo Animado", "Hayao Miyazaki",
                                                List.of("Animação", "Fantasia", "Romance"), 119, 2004,
                                                "Uma jovem rapariga maldita recebe ajuda de um misterioso mago e é levada a um castelo animado, onde descobre que há mais do que parece.",
                                                8.4, 8.2,
                                                "Uma animação deslumbrante com uma história de fantasia romantizada, personagens encantadores e uma trilha sonora memorável.",
                                                "https://image.tmdb.org/t/p/w500/3cyjYtLWCW7A4s5rT2gj2jqVzlR.jpg",
                                                List.of(new Platform("Netflix",
                                                                "https://www.netflix.com/title/70047291"),
                                                                new Platform("Amazon Prime Video",
                                                                                "https://www.primevideo.com/detail/0GUKG9C1Q5KJZP2XQYB8YVZP7"))),
                                criarFilme("14", "Túmulo dos Vagalumes", "Isao Takahata",
                                                List.of("Animação", "Drama", "Guerra"), 89, 1988,
                                                "Dois órfãos enfrentam a pobreza e o bombardeio durante a Segunda Guerra Mundial, com um menino mais velho protegendo sua irmã mais jovem.",
                                                8.5, 8.3,
                                                "Um filme de animação profundamente comovente que oferece uma perspectiva humanista sobre os horrores da guerra através dos olhos de crianças.",
                                                "https://image.tmdb.org/t/p/w500/xZvqsfXamQgNAeq4eEL1WEeMsQG.jpg",
                                                List.of(new Platform("Netflix",
                                                                "https://www.netflix.com/title/80216616"),
                                                                new Platform("Amazon Prime Video",
                                                                                "https://www.primevideo.com/detail/0LCRMB4Z0UJVWF47NZHR5K2R94"))),
                                criarFilme("15", "O Homem Invisível", "Leigh Whannell",
                                                List.of("Horror", "Thriller", "Ficção Científica"), 125, 2020,
                                                "Uma mulher é aterrorizada por seu ex abusivo que encontra um meio de se tornar invisível, permitindo-lhe perseguir-la impunemente.",
                                                7.4, 7.5,
                                                "Um thriller de horror moderno que usa o conceito de invisibilidade de forma criativa, gerando tensão através da ameaça invisível e do gaslighting psicológico.",
                                                "https://image.tmdb.org/t/p/w500/djbD2pi3J5wiM5G8eaEXKVjrVB.jpg",
                                                List.of(new Platform("Netflix",
                                                                "https://www.netflix.com/title/81016255"),
                                                                new Platform("Amazon Prime Video",
                                                                                "https://www.primevideo.com/detail/0LCRMB4Z0UJVWF47NZHR5K2R94"))),
                                criarFilme("16", "10 Coisas Que Eu Odeio em Você", "Gil Junger",
                                                List.of("Comédia", "Romance", "Drama"), 97, 1999,
                                                "Uma tentativa de conquistar uma jovem mulher que odeia romantismo se transforma em uma história de romance genuíno quando dois meninos fazem uma aposta.",
                                                7.6, 7.7,
                                                "Uma comédia romântica leve e divertida baseada em Shakespeare que encanta com seu humor e química entre os protagonistas.",
                                                "https://image.tmdb.org/t/p/w500/uxDMWYVz9HY85UQndhQ33r5P9mE.jpg",
                                                List.of(new Platform("Netflix",
                                                                "https://www.netflix.com/title/60004051"),
                                                                new Platform("Amazon Prime Video",
                                                                                "https://www.primevideo.com/detail/0GUKG9C1Q5KJZP2XQYB8YVZP7"))));
        }

        private static Movie criarFilme(String id, String titulo, String autor,
                                        List<String> generos, Integer duracao, Integer anoLancamento,
                                        String sinopse, Double notaDivina, Double notaPublico,
                                        String motivoRecomendacao, String poster, List<Platform> plataformas) {
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
