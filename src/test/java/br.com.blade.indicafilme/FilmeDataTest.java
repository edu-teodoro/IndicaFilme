package br.com.blade.indicafilme;

import br.com.blade.indicafilme.model.Movie;
import br.com.blade.indicafilme.model.StatusFilme;
import br.com.blade.indicafilme.repository.FilmeData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmeDataTest {

    // ========== Testes de Quantidade de Filmes ==========

    @Test
    void todos_retorna16Filmes() {
        List<Movie> filmes = FilmeData.todos();
        assertEquals(16, filmes.size());
    }

    @Test
    void todos_naoRetornaNull() {
        List<Movie> filmes = FilmeData.todos();
        assertNotNull(filmes);
    }

    @Test
    void todos_listaImutavel() {
        List<Movie> filmes = FilmeData.todos();
        assertThrows(UnsupportedOperationException.class, () -> filmes.add(new Movie()));
    }

    // ========== Testes de IDs Únicos ==========

    @Test
    void todos_todosOsFilmesTemId() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().allMatch(f -> f.getId() != null && !f.getId().isEmpty()));
    }

    @Test
    void todos_idsSequenciaisde1a16() {
        List<Movie> filmes = FilmeData.todos();
        for (int i = 1; i <= 16; i++) {
            String expectedId = String.valueOf(i);
            assertTrue(filmes.stream().anyMatch(f -> f.getId().equals(expectedId)),
                    "Filme com ID " + i + " não encontrado");
        }
    }

    @Test
    void todos_idsUnicos() {
        List<Movie> filmes = FilmeData.todos();
        long uniqueIds = filmes.stream().map(Movie::getId).distinct().count();
        assertEquals(16, uniqueIds);
    }

    // ========== Testes de Campos Obrigatórios ==========

    @Test
    void todos_todoOsFilmesTemTitulo() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().allMatch(f -> f.getTitulo() != null && !f.getTitulo().isEmpty()),
                "Existe filme sem título");
    }

    @Test
    void todos_todoOsFilmesTemAutor() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().allMatch(f -> f.getAutor() != null && !f.getAutor().isEmpty()),
                "Existe filme sem autor/diretor");
    }

    @Test
    void todos_todoOsFilmesTemGeneros() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().allMatch(f -> f.getGeneros() != null && !f.getGeneros().isEmpty()),
                "Existe filme sem gêneros");
    }

    @Test
    void todos_todoOsFilmesTemDuracao() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().allMatch(f -> f.getDuracao() != null && f.getDuracao() > 0),
                "Existe filme sem duração válida");
    }

    @Test
    void todos_todoOsFilmesTemAnoLancamento() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().allMatch(f -> f.getAnoLancamento() != null && f.getAnoLancamento() > 0),
                "Existe filme sem ano de lançamento");
    }

    @Test
    void todos_todoOsFilmesTemSinopse() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().allMatch(f -> f.getSinopse() != null && !f.getSinopse().isEmpty()),
                "Existe filme sem sinopse");
    }

    @Test
    void todos_todoOsFilmesTemNotaDivina() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().allMatch(f -> f.getNotaDivina() != null && f.getNotaDivina() >= 0.0 && f.getNotaDivina() <= 10.0),
                "Existe filme com nota divina inválida");
    }

    @Test
    void todos_todoOsFilmesTemNotaPublico() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().allMatch(f -> f.getNotaPublico() != null && f.getNotaPublico() >= 0.0 && f.getNotaPublico() <= 10.0),
                "Existe filme com nota público inválida");
    }

    @Test
    void todos_todoOsFilmesTemMotivoRecomendacao() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().allMatch(f -> f.getMotivoRecomendacao() != null && !f.getMotivoRecomendacao().isEmpty()),
                "Existe filme sem motivo de recomendação");
    }

    @Test
    void todos_todoOsFilmesTemPoster() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().allMatch(f -> f.getPoster() != null && !f.getPoster().isEmpty()),
                "Existe filme sem poster");
    }

    @Test
    void todos_todoOsFilmesTemPlataformas() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().allMatch(f -> f.getPlataformas() != null && !f.getPlataformas().isEmpty()),
                "Existe filme sem plataformas");
    }

    @Test
    void todos_todoOsFilmesTemStatus() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().allMatch(f -> f.getStatus() == StatusFilme.ATIVO),
                "Existe filme com status diferente de ATIVO");
    }

    // ========== Testes de Validação de Dados ==========

    @Test
    void todos_duracaoValida() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().allMatch(f -> f.getDuracao() >= 30 && f.getDuracao() <= 300),
                "Duração fora do intervalo esperado");
    }

    @Test
    void todos_anoLancamentoValido() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().allMatch(f -> f.getAnoLancamento() >= 1888 && f.getAnoLancamento() <= 2100),
                "Ano de lançamento inválido");
    }

    @Test
    void todos_generosNaoVarias() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().allMatch(f -> f.getGeneros().size() >= 1 && f.getGeneros().size() <= 5),
                "Filme com quantidade de gêneros inválida");
    }

    @Test
    void todos_plataformasNaoVarias() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().allMatch(f -> f.getPlataformas().size() >= 1 && f.getPlataformas().size() <= 6),
                "Filme com quantidade de plataformas inválida");
    }

    @Test
    void todos_posterUrlValida() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().allMatch(f -> f.getPoster().startsWith("http")),
                "Poster com URL inválida");
    }

    @Test
    void todos_plataformasUrlValida() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().allMatch(f -> f.getPlataformas().stream()
                .allMatch(p -> p.url() != null && (p.url().startsWith("http") || p.url().isEmpty()))),
                "Plataforma com URL inválida");
    }

    // ========== Testes de Conteúdo Específico ==========

    @Test
    void todos_contemSenhorDosAneis() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().anyMatch(f -> f.getTitulo().contains("Senhor dos Anéis")),
                "Senhor dos Anéis não encontrado");
    }

    @Test
    void todos_contemInterestelar() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().anyMatch(f -> f.getTitulo().contains("Interestelar")),
                "Interestelar não encontrado");
    }

    @Test
    void todos_contemParasita() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().anyMatch(f -> f.getTitulo().contains("Parasita")),
                "Parasita não encontrado");
    }

    @Test
    void todos_contemDeVoltaParaoFuturo() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().anyMatch(f -> f.getTitulo().contains("De Volta para o Futuro")),
                "De Volta para o Futuro não encontrado");
    }

    @Test
    void todos_contemClubedaluta() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().anyMatch(f -> f.getTitulo().contains("Clube da Luta")),
                "Clube da Luta não encontrado");
    }

    @Test
    void todos_contemWhiplash() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().anyMatch(f -> f.getTitulo().contains("Whiplash")),
                "Whiplash não encontrado");
    }

    @Test
    void todos_contemOldboy() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().anyMatch(f -> f.getTitulo().contains("Oldboy")),
                "Oldboy não encontrado");
    }

    @Test
    void todos_contemAViagemDeChihiro() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().anyMatch(f -> f.getTitulo().contains("A Viagem de Chihiro")),
                "A Viagem de Chihiro não encontrado");
    }

    @Test
    void todos_contemSeven() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().anyMatch(f -> f.getTitulo().contains("Seven")),
                "Seven não encontrado");
    }

    @Test
    void todos_contemAsCronicasSpiderwick() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().anyMatch(f -> f.getTitulo().contains("Crônicas") || f.getTitulo().contains("Cronicas")),
                "As Crônicas de Spiderwick não encontrado");
    }

    @Test
    void todos_contemOPalidoOlhoAzul() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().anyMatch(f -> f.getTitulo().contains("Pálido") || f.getTitulo().contains("Palido")),
                "O Pálido Olho Azul não encontrado");
    }

    @Test
    void todos_contemPaprika() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().anyMatch(f -> f.getTitulo().contains("Paprika")),
                "Paprika não encontrado");
    }

    @Test
    void todos_contemCasteloAnimado() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().anyMatch(f -> f.getTitulo().contains("Castelo")),
                "Castelo Animado não encontrado");
    }

    @Test
    void todos_contemTumuloDosvagalumes() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().anyMatch(f -> f.getTitulo().contains("Túmulo") || f.getTitulo().contains("Tumulo")),
                "Túmulo dos Vagalumes não encontrado");
    }

    @Test
    void todos_contemOHomemInvisivel() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().anyMatch(f -> f.getTitulo().contains("Homem Invisível")),
                "O Homem Invisível não encontrado");
    }

    @Test
    void todos_contem10CoisasQueEuOdeio() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().anyMatch(f -> f.getTitulo().contains("10")),
                "10 Coisas Que Eu Odeio em Você não encontrado");
    }

    // ========== Testes de Diretores ==========

    @Test
    void todos_filmesComDiretoresValidos() {
        List<Movie> filmes = FilmeData.todos();
        // Apenas valida que todos os filmes têm um diretor preenchido
        assertTrue(filmes.stream().allMatch(f -> f.getAutor() != null && !f.getAutor().isEmpty()),
                "Algum filme não tem diretor preenchido");
    }

    // ========== Testes de Notas ==========

    @Test
    void todos_notaDivinaNuncaMenorQue6() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().allMatch(f -> f.getNotaDivina() >= 6.0),
                "Existe filme com nota divina menor que 6");
    }

    @Test
    void todos_notaPublicoNuncaMenorQue6() {
        List<Movie> filmes = FilmeData.todos();
        assertTrue(filmes.stream().allMatch(f -> f.getNotaPublico() >= 6.0),
                "Existe filme com nota público menor que 6");
    }

    @Test
    void todos_notasValidas() {
        List<Movie> filmes = FilmeData.todos();
        for (Movie filme : filmes) {
            assertTrue(filme.getNotaDivina() >= 0.0 && filme.getNotaDivina() <= 10.0,
                    "Nota divina inválida para " + filme.getTitulo());
            assertTrue(filme.getNotaPublico() >= 0.0 && filme.getNotaPublico() <= 10.0,
                    "Nota público inválida para " + filme.getTitulo());
        }
    }

    // ========== Testes de Idempotência ==========

    @Test
    void todos_sempreRetornaOsMesmosFilmes() {
        List<Movie> filmes1 = FilmeData.todos();
        List<Movie> filmes2 = FilmeData.todos();
        
        assertEquals(filmes1.size(), filmes2.size());
        for (int i = 0; i < filmes1.size(); i++) {
            assertEquals(filmes1.get(i).getId(), filmes2.get(i).getId());
            assertEquals(filmes1.get(i).getTitulo(), filmes2.get(i).getTitulo());
        }
    }

    // ========== Testes de Estrutura ==========

    @Test
    void todos_ordemDoFilmesConsistente() {
        List<Movie> filmes = FilmeData.todos();
        for (int i = 0; i < filmes.size(); i++) {
            assertEquals(String.valueOf(i + 1), filmes.get(i).getId(),
                    "Ordem dos filmes alterada ou IDs inconsistentes");
        }
    }

    @Test
    void todos_listaRetornadoTemExatamente16() {
        List<Movie> filmes = FilmeData.todos();
        assertEquals(16, filmes.size(), "Número de filmes diferente de 16");
    }
}


