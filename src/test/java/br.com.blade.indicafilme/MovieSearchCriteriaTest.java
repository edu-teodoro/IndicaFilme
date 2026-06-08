package br.com.blade.indicafilme;

import br.com.blade.indicafilme.dto.MovieSearchCriteria;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovieSearchCriteriaTest {

    // ========== Testes de Normalização de Gêneros ==========

    @Test
    void construtor_normalizaGenerosAutomaticamente() {
        MovieSearchCriteria criteria = new MovieSearchCriteria(List.of("Ação"), null, null);
        assertEquals("ACAO", criteria.generos().get(0));
    }

    @Test
    void construtor_normalizaMultiplosGeneros() {
        MovieSearchCriteria criteria = new MovieSearchCriteria(
                List.of("Ação", "comédia", "DRAMA"), null, null
        );
        assertEquals("ACAO", criteria.generos().get(0));
        assertEquals("COMEDIA", criteria.generos().get(1));
        assertEquals("DRAMA", criteria.generos().get(2));
    }

    @Test
    void construtor_removeEspacosEAcentos() {
        MovieSearchCriteria criteria = new MovieSearchCriteria(
                List.of("  Ação  ", "  Comé dia  "), null, null
        );
        assertEquals("ACAO", criteria.generos().get(0));
        assertEquals("COMEDIA", criteria.generos().get(1));
    }

    @Test
    void normalizar_generoComAcentoMultiplo() {
        String resultado = MovieSearchCriteria.normalizar("Ficção Científica");
        assertEquals("FICCAO CIENTIFICA", resultado);
    }

    @Test
    void normalizar_generoVazio() {
        String resultado = MovieSearchCriteria.normalizar("");
        assertNull(resultado);
    }

    @Test
    void normalizar_generoNull() {
        String resultado = MovieSearchCriteria.normalizar(null);
        assertNull(resultado);
    }

    @Test
    void normalizar_generoComEspacosEmBranco() {
        String resultado = MovieSearchCriteria.normalizar("   ");
        assertNull(resultado);
    }

    @Test
    void normalizar_generoMisto() {
        String resultado = MovieSearchCriteria.normalizar("AçÃo DramÁ");
        assertEquals("ACAO DRAMA", resultado);
    }

    // ========== Testes de Normalização de Durações ==========

    @Test
    void construtor_normalizaDuracoes() {
        MovieSearchCriteria criteria = new MovieSearchCriteria(
                null, List.of("curta", "MEDIA", "  longa  "), null
        );
        assertEquals("CURTA", criteria.duracoes().get(0));
        assertEquals("MEDIA", criteria.duracoes().get(1));
        assertEquals("LONGA", criteria.duracoes().get(2));
    }

    @Test
    void construtor_duracoesList() {
        MovieSearchCriteria criteria = new MovieSearchCriteria(
                null, List.of("CURTA"), null
        );
        assertEquals(1, criteria.duracoes().size());
        assertEquals("CURTA", criteria.duracoes().get(0));
    }

    @Test
    void construtor_duracoesNull() {
        MovieSearchCriteria criteria = new MovieSearchCriteria(
                null, null, null
        );
        assertNull(criteria.duracoes());
    }

    // ========== Testes de Normalização de Décadas ==========

    @Test
    void construtor_converteAnoParaDecada() {
        MovieSearchCriteria criteria = new MovieSearchCriteria(
                null, null, List.of(1995, 2005, 1988)
        );
        assertEquals(1990, criteria.decadas().get(0));
        assertEquals(2000, criteria.decadas().get(1));
        assertEquals(1980, criteria.decadas().get(2));
    }

    @Test
    void construtor_decadaExata() {
        MovieSearchCriteria criteria = new MovieSearchCriteria(
                null, null, List.of(1990, 2000, 2020)
        );
        assertEquals(1990, criteria.decadas().get(0));
        assertEquals(2000, criteria.decadas().get(1));
        assertEquals(2020, criteria.decadas().get(2));
    }

    @Test
    void construtor_anoNoFinalDaDecada() {
        MovieSearchCriteria criteria = new MovieSearchCriteria(
                null, null, List.of(1999)
        );
        assertEquals(1990, criteria.decadas().get(0));
    }

    @Test
    void construtor_decedasNull() {
        MovieSearchCriteria criteria = new MovieSearchCriteria(
                null, null, null
        );
        assertNull(criteria.decadas());
    }

    @Test
    void construtor_anoZero() {
        MovieSearchCriteria criteria = new MovieSearchCriteria(
                null, null, List.of(0)
        );
        assertEquals(0, criteria.decadas().get(0));
    }

    // ========== Testes de Casos Compostos ==========

    @Test
    void construtor_todasAsNormalizacoes() {
        MovieSearchCriteria criteria = new MovieSearchCriteria(
                List.of("Ação", "comédia"),
                List.of("curta", "  MEDIA  "),
                List.of(1995, 2010)
        );
        assertEquals("ACAO", criteria.generos().get(0));
        assertEquals("COMEDIA", criteria.generos().get(1));
        assertEquals("CURTA", criteria.duracoes().get(0));
        assertEquals("MEDIA", criteria.duracoes().get(1));
        assertEquals(1990, criteria.decadas().get(0));
        assertEquals(2010, criteria.decadas().get(1));
    }

    @Test
    void construtor_todosNulosEVazios() {
        MovieSearchCriteria criteria = new MovieSearchCriteria(null, null, null);
        assertNull(criteria.generos());
        assertNull(criteria.duracoes());
        assertNull(criteria.decadas());
    }

    @Test
    void construtor_listaVaziaGeneros() {
        MovieSearchCriteria criteria = new MovieSearchCriteria(List.of(), null, null);
        assertNotNull(criteria.generos());
        assertTrue(criteria.generos().isEmpty());
    }

    @Test
    void construtor_listaVaziaDuracoes() {
        MovieSearchCriteria criteria = new MovieSearchCriteria(null, List.of(), null);
        assertNotNull(criteria.duracoes());
        assertTrue(criteria.duracoes().isEmpty());
    }

    @Test
    void construtor_listaVaziaDecadas() {
        MovieSearchCriteria criteria = new MovieSearchCriteria(null, null, List.of());
        assertNotNull(criteria.decadas());
        assertTrue(criteria.decadas().isEmpty());
    }

    @Test
    void normalizar_generoComCaracteresEspeciais() {
        String resultado = MovieSearchCriteria.normalizar("Sci-Fi & Fantasy");
        assertEquals("SCI-FI & FANTASY", resultado);
    }

    @Test
    void normalizar_generoComNumeros() {
        String resultado = MovieSearchCriteria.normalizar("20 de Abril");
        assertEquals("20 DE ABRIL", resultado);
    }

    @Test
    void construtor_decadasComNumerosNegativos() {
        MovieSearchCriteria criteria = new MovieSearchCriteria(
                null, null, List.of(-1990)
        );
        assertEquals(-1990, criteria.decadas().get(0));
    }

    @Test
    void construtor_generosComNulosNaLista() {
        MovieSearchCriteria criteria = new MovieSearchCriteria(
                List.of("Ação", null, "Drama"), null, null
        );
        assertEquals("ACAO", criteria.generos().get(0));
        assertNull(criteria.generos().get(1));
        assertEquals("DRAMA", criteria.generos().get(2));
    }
}
