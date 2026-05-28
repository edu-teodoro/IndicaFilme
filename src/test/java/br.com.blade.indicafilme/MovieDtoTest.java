package br.com.blade.indicafilme;

import br.com.blade.indicafilme.dto.MovieDto;
import br.com.blade.indicafilme.model.Movie;
import br.com.blade.indicafilme.model.Plataform;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovieDtoTest {
    @Test
    void fromMovie_populaCamposCorretamente() {
        Movie movie = new Movie();
        movie.setId("1");
        movie.setTitulo("Filme Teste");
        movie.setAutor("Autor");
        movie.setGeneros(List.of("Ação", "Drama"));
        movie.setDuracao(120);
        movie.setAnoLancamento(2020);
        movie.setSinopse("Sinopse");
        movie.setNotaDivina(7.5);
        movie.setNotaPublico(6.0);
        movie.setMotivoRecomendacao("Motivo");
        Plataform netflix = new Plataform("Netflix", "https://netflix.com");
        movie.setPlataformas(List.of(netflix));
        movie.setPoster("poster.jpg");

        MovieDto dto = MovieDto.fromMovie(movie);
        assertEquals("1", dto.getId());
        assertEquals("Filme Teste", dto.getTitulo());
        assertEquals("Autor", dto.getAutor());
        assertEquals(List.of("Ação", "Drama"), dto.getGeneros());
        assertEquals(120, dto.getDuracao());
        assertEquals(2020, dto.getAnolancamento());
        assertEquals("Sinopse", dto.getSinopse());
        assertEquals("7+", dto.getNotaDivina());
        assertEquals("6", dto.getNotaPublico());
        assertEquals("6+", dto.getMediaNotas());
        assertEquals("Motivo", dto.getMotivoRecomendacao());
        assertEquals(1, dto.getPlataformas().size());
        assertEquals("poster.jpg", dto.getPoster());
    }

    @Test
    void fromMovie_notasNulas() {
        Movie movie = new Movie();
        MovieDto dto = MovieDto.fromMovie(movie);
        assertEquals("_", dto.getNotaDivina());
        assertEquals("_", dto.getNotaPublico());
        assertEquals("_", dto.getMediaNotas());
    }

    // ========== Testes de Formatação de Notas ==========

    @Test
    void formatarNota_notaMaiorQueSeis() {
        Movie movie = new Movie();
        movie.setNotaDivina(7.8);
        movie.setNotaPublico(null);
        MovieDto dto = MovieDto.fromMovie(movie);
        assertEquals("7+", dto.getNotaDivina());
    }

    @Test
    void formatarNota_notaMenorqueSeis() {
        Movie movie = new Movie();
        movie.setNotaDivina(5.2);
        movie.setNotaPublico(null);
        MovieDto dto = MovieDto.fromMovie(movie);
        assertEquals("5", dto.getNotaDivina());
    }

    @Test
    void formatarNota_notaExatamenteSeis() {
        Movie movie = new Movie();
        movie.setNotaDivina(6.0);
        movie.setNotaPublico(null);
        MovieDto dto = MovieDto.fromMovie(movie);
        assertEquals("6", dto.getNotaDivina());
    }

    @Test
    void formatarNota_notaComDecimalMenorQueUmaUnidade() {
        Movie movie = new Movie();
        movie.setNotaDivina(8.3);
        movie.setNotaPublico(null);
        MovieDto dto = MovieDto.fromMovie(movie);
        assertEquals("8", dto.getNotaDivina());
    }

    @Test
    void formatarNota_notaComDecimalMaiorQueUmaUnidade() {
        Movie movie = new Movie();
        movie.setNotaDivina(8.6);
        movie.setNotaPublico(null);
        MovieDto dto = MovieDto.fromMovie(movie);
        assertEquals("8+", dto.getNotaDivina());
    }

    @Test
    void formatarNota_notaZero() {
        Movie movie = new Movie();
        movie.setNotaDivina(0.0);
        movie.setNotaPublico(null);
        MovieDto dto = MovieDto.fromMovie(movie);
        assertEquals("0", dto.getNotaDivina());
    }

    @Test
    void formatarNota_notaDez() {
        Movie movie = new Movie();
        movie.setNotaDivina(10.0);
        movie.setNotaPublico(null);
        MovieDto dto = MovieDto.fromMovie(movie);
        assertEquals("10+", dto.getNotaDivina());
    }

    // ========== Testes de Cálculo de Média ==========

    @Test
    void calcularMedia_ambasNotasNull() {
        Movie movie = new Movie();
        movie.setNotaDivina(null);
        movie.setNotaPublico(null);
        MovieDto dto = MovieDto.fromMovie(movie);
        assertEquals("_", dto.getMediaNotas());
    }

    @Test
    void calcularMedia_apenasNotaPubico() {
        Movie movie = new Movie();
        movie.setNotaDivina(null);
        movie.setNotaPublico(8.5);
        MovieDto dto = MovieDto.fromMovie(movie);
        assertEquals("8+", dto.getMediaNotas());
    }

    @Test
    void calcularMedia_apenasNotaDivina() {
        Movie movie = new Movie();
        movie.setNotaDivina(9.2);
        movie.setNotaPublico(null);
        MovieDto dto = MovieDto.fromMovie(movie);
        assertEquals("9+", dto.getMediaNotas());
    }

    @Test
    void calcularMedia_mediaPar() {
        Movie movie = new Movie();
        movie.setNotaDivina(8.0);
        movie.setNotaPublico(8.0);
        MovieDto dto = MovieDto.fromMovie(movie);
        assertEquals("8+", dto.getMediaNotas());
    }

    @Test
    void calcularMedia_mediaResultaEmValorComDecimalBaixo() {
        Movie movie = new Movie();
        movie.setNotaDivina(7.0);
        movie.setNotaPublico(8.0);
        MovieDto dto = MovieDto.fromMovie(movie);
        // (7.0 + 8.0) / 2 = 7.5 -> 7.5 > 6 -> "7+"
        assertEquals("7+", dto.getMediaNotas());
    }

    @Test
    void calcularMedia_mediaResultaEmValorComDecimalAlto() {
        Movie movie = new Movie();
        movie.setNotaDivina(5.0);
        movie.setNotaPublico(4.0);
        MovieDto dto = MovieDto.fromMovie(movie);
        // (5.0 + 4.0) / 2 = 4.5 -> 4.5 < 6 -> "4"
        assertEquals("4", dto.getMediaNotas());
    }

    // ========== Testes de Edge Cases ==========

    @Test
    void fromMovie_camposNulosOuVazios() {
        Movie movie = new Movie();
        movie.setId(null);
        movie.setTitulo(null);
        movie.setAutor(null);
        movie.setGeneros(null);
        movie.setDuracao(null);
        movie.setAnoLancamento(null);
        movie.setSinopse(null);
        movie.setMotivoRecomendacao(null);
        movie.setPlataformas(null);
        movie.setPoster(null);

        MovieDto dto = MovieDto.fromMovie(movie);
        assertNull(dto.getId());
        assertNull(dto.getTitulo());
        assertNull(dto.getAutor());
        assertNull(dto.getGeneros());
        assertNull(dto.getDuracao());
        assertNull(dto.getAnolancamento());
        assertNull(dto.getSinopse());
        assertNull(dto.getMotivoRecomendacao());
        assertNull(dto.getPlataformas());
        assertNull(dto.getPoster());
    }

    @Test
    void fromMovie_listaGenerosVazia() {
        Movie movie = new Movie();
        movie.setTitulo("Filme");
        movie.setGeneros(List.of());
        MovieDto dto = MovieDto.fromMovie(movie);
        assertNotNull(dto.getGeneros());
        assertTrue(dto.getGeneros().isEmpty());
    }

    @Test
    void fromMovie_multiplosPlatformas() {
        Movie movie = new Movie();
        movie.setTitulo("Filme");
        Plataform p1 = new Plataform("Netflix", "url1");
        Plataform p2 = new Plataform("Prime", "url2");
        Plataform p3 = new Plataform("Disney", "url3");
        movie.setPlataformas(List.of(p1, p2, p3));
        MovieDto dto = MovieDto.fromMovie(movie);
        assertEquals(3, dto.getPlataformas().size());
    }

    @Test
    void formatarNota_notaComMuitasDecimais() {
        Movie movie = new Movie();
        movie.setNotaDivina(7.999);
        movie.setNotaPublico(null);
        MovieDto dto = MovieDto.fromMovie(movie);
        assertEquals("7+", dto.getNotaDivina());
    }

    @Test
    void formatarNota_notaExatamente6_5() {
        Movie movie = new Movie();
        movie.setNotaDivina(6.5);
        movie.setNotaPublico(null);
        MovieDto dto = MovieDto.fromMovie(movie);
        // 6.5 > 6.0 -> "6+"
        assertEquals("6+", dto.getNotaDivina());
    }
}
