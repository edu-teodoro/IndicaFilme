package br.com.blade.indicafilme;

import br.com.blade.indicafilme.service.MovieCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MovieCacheServiceTest {
    private MovieCacheService cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new MovieCacheService();
    }

    // ========== Testes de Registro e Leitura ==========

    @Test
    void registrarUltimoFilme_e_getUltimoFilmeId() {
        String sessionId = "sessao1";
        String filmeId = "filme1";
        
        assertNull(cacheService.getUltimoFilmeId(sessionId));
        cacheService.registrarUltimoFilme(sessionId, filmeId);
        assertEquals(filmeId, cacheService.getUltimoFilmeId(sessionId));
    }

    @Test
    void registrarUltimoFilme_multiplaSessoes() {
        cacheService.registrarUltimoFilme("sessao1", "filme1");
        cacheService.registrarUltimoFilme("sessao2", "filme2");
        cacheService.registrarUltimoFilme("sessao3", "filme3");

        assertEquals("filme1", cacheService.getUltimoFilmeId("sessao1"));
        assertEquals("filme2", cacheService.getUltimoFilmeId("sessao2"));
        assertEquals("filme3", cacheService.getUltimoFilmeId("sessao3"));
    }

    @Test
    void registrarUltimoFilme_sobresecreve() {
        cacheService.registrarUltimoFilme("sessao1", "filme1");
        assertEquals("filme1", cacheService.getUltimoFilmeId("sessao1"));
        
        cacheService.registrarUltimoFilme("sessao1", "filme2");
        assertEquals("filme2", cacheService.getUltimoFilmeId("sessao1"));
    }

    @Test
    void getUltimoFilmeId_sessoesIsoladas() {
        cacheService.registrarUltimoFilme("sessao1", "filme1");
        assertNull(cacheService.getUltimoFilmeId("sessao2"));
    }

    @Test
    void getUltimoFilmeId_sessaoNuncaRegistrada() {
        assertNull(cacheService.getUltimoFilmeId("sessao-inexistente"));
    }

    // ========== Testes de isUltimoFilme ==========

    @Test
    void isUltimoFilme_verdadeiro() {
        cacheService.registrarUltimoFilme("sessao1", "filme1");
        assertTrue(cacheService.isUltimoFilme("sessao1", "filme1"));
    }

    @Test
    void isUltimoFilme_falso() {
        cacheService.registrarUltimoFilme("sessao1", "filme1");
        assertFalse(cacheService.isUltimoFilme("sessao1", "filme2"));
    }

    @Test
    void isUltimoFilme_sessaoVazia() {
        assertFalse(cacheService.isUltimoFilme("sessao1", "filme1"));
    }

    @Test
    void isUltimoFilme_filmeIdNull() {
        cacheService.registrarUltimoFilme("sessao1", "filme1");
        assertFalse(cacheService.isUltimoFilme("sessao1", null));
    }

    @Test
    void isUltimoFilme_filmeIdVazio() {
        cacheService.registrarUltimoFilme("sessao1", "filme1");
        assertFalse(cacheService.isUltimoFilme("sessao1", ""));
    }

    @Test
    void isUltimoFilme_multiplaSessoes() {
        cacheService.registrarUltimoFilme("sessao1", "filme1");
        cacheService.registrarUltimoFilme("sessao2", "filme2");

        assertTrue(cacheService.isUltimoFilme("sessao1", "filme1"));
        assertTrue(cacheService.isUltimoFilme("sessao2", "filme2"));
        assertFalse(cacheService.isUltimoFilme("sessao1", "filme2"));
        assertFalse(cacheService.isUltimoFilme("sessao2", "filme1"));
    }

    @Test
    void isUltimoFilme_aposAtualizacao() {
        cacheService.registrarUltimoFilme("sessao1", "filme1");
        assertTrue(cacheService.isUltimoFilme("sessao1", "filme1"));
        
        cacheService.registrarUltimoFilme("sessao1", "filme2");
        assertFalse(cacheService.isUltimoFilme("sessao1", "filme1"));
        assertTrue(cacheService.isUltimoFilme("sessao1", "filme2"));
    }

    // ========== Testes de limparSessao ==========

    @Test
    void limparSessao_removeRegistro() {
        cacheService.registrarUltimoFilme("sessao1", "filme1");
        assertEquals("filme1", cacheService.getUltimoFilmeId("sessao1"));
        
        cacheService.limparSessao("sessao1");
        assertNull(cacheService.getUltimoFilmeId("sessao1"));
    }

    @Test
    void limparSessao_naoAfetaOutrasSessoes() {
        cacheService.registrarUltimoFilme("sessao1", "filme1");
        cacheService.registrarUltimoFilme("sessao2", "filme2");
        
        cacheService.limparSessao("sessao1");
        assertNull(cacheService.getUltimoFilmeId("sessao1"));
        assertEquals("filme2", cacheService.getUltimoFilmeId("sessao2"));
    }

    @Test
    void limparSessao_sessaoInexistente() {
        assertDoesNotThrow(() -> cacheService.limparSessao("sessao-inexistente"));
    }

    @Test
    void limparSessao_falsoPositivo() {
        cacheService.registrarUltimoFilme("sessao1", "filme1");
        cacheService.limparSessao("sessao1");
        
        assertFalse(cacheService.isUltimoFilme("sessao1", "filme1"));
    }

    // ========== Testes de Fluxo Completo ==========

    @Test
    void fluxo_regitrarConsultarAtualizarLimpar() {
        cacheService.registrarUltimoFilme("sessao1", "filme1");
        assertEquals("filme1", cacheService.getUltimoFilmeId("sessao1"));
        assertTrue(cacheService.isUltimoFilme("sessao1", "filme1"));
        
        cacheService.registrarUltimoFilme("sessao1", "filme2");
        assertEquals("filme2", cacheService.getUltimoFilmeId("sessao1"));
        assertFalse(cacheService.isUltimoFilme("sessao1", "filme1"));
        
        cacheService.limparSessao("sessao1");
        assertNull(cacheService.getUltimoFilmeId("sessao1"));
    }

    @Test
    void fluxo_multiploSorteios() {
        String sessionId = "sessao-sorteio";
        
        // Primeiro sorteio
        cacheService.registrarUltimoFilme(sessionId, "filme1");
        assertTrue(cacheService.isUltimoFilme(sessionId, "filme1"));
        
        // Segundo sorteio (evita repetir filme1)
        cacheService.registrarUltimoFilme(sessionId, "filme2");
        assertFalse(cacheService.isUltimoFilme(sessionId, "filme1"));
        assertTrue(cacheService.isUltimoFilme(sessionId, "filme2"));
        
        // Terceiro sorteio
        cacheService.registrarUltimoFilme(sessionId, "filme3");
        assertFalse(cacheService.isUltimoFilme(sessionId, "filme2"));
        assertTrue(cacheService.isUltimoFilme(sessionId, "filme3"));
    }

    // ========== Testes de Edge Cases ==========

    @Test
    void edge_case_filmeIdComCaracteresEspeciais() {
        String filmeId = "filme@#$%^&*()_+-=[]{}|;:',.<>?/";
        cacheService.registrarUltimoFilme("sessao1", filmeId);
        assertEquals(filmeId, cacheService.getUltimoFilmeId("sessao1"));
        assertTrue(cacheService.isUltimoFilme("sessao1", filmeId));
    }

    @Test
    void edge_case_sessionIdComCaracteresEspeciais() {
        String sessionId = "sessao@#$%^&*()_+-=[]{}|;:',.<>?/";
        cacheService.registrarUltimoFilme(sessionId, "filme1");
        assertEquals("filme1", cacheService.getUltimoFilmeId(sessionId));
    }

    @Test
    void edge_case_filmeIdMuitoLongo() {
        String filmeId = "A".repeat(10000);
        cacheService.registrarUltimoFilme("sessao1", filmeId);
        assertEquals(filmeId, cacheService.getUltimoFilmeId("sessao1"));
    }

    @Test
    void edge_case_sessionIdMuitoLongo() {
        String sessionId = "B".repeat(10000);
        cacheService.registrarUltimoFilme(sessionId, "filme1");
        assertEquals("filme1", cacheService.getUltimoFilmeId(sessionId));
    }

    @Test
    void edge_case_numeroGrandeDeSessoes() {
        for (int i = 0; i < 100; i++) {
            cacheService.registrarUltimoFilme("sessao" + i, "filme" + i);
        }
        
        assertEquals("filme0", cacheService.getUltimoFilmeId("sessao0"));
        assertEquals("filme50", cacheService.getUltimoFilmeId("sessao50"));
        assertEquals("filme99", cacheService.getUltimoFilmeId("sessao99"));
    }

    @Test
    void edge_case_mesmoFilmeEmMultiplasSessoes() {
        cacheService.registrarUltimoFilme("sessao1", "filme-popular");
        cacheService.registrarUltimoFilme("sessao2", "filme-popular");
        cacheService.registrarUltimoFilme("sessao3", "filme-popular");

        assertTrue(cacheService.isUltimoFilme("sessao1", "filme-popular"));
        assertTrue(cacheService.isUltimoFilme("sessao2", "filme-popular"));
        assertTrue(cacheService.isUltimoFilme("sessao3", "filme-popular"));
    }

    @Test
    void edge_case_registrarNull() {
        assertDoesNotThrow(() -> cacheService.registrarUltimoFilme("sessao1", null));
    }

    @Test
    void edge_case_sessaoNullGetUltimo() {
        assertNull(cacheService.getUltimoFilmeId(null));
    }

    @Test
    void edge_case_sessaoNullIsUltimo() {
        assertFalse(cacheService.isUltimoFilme(null, "filme1"));
    }

    @Test
    void edge_case_sessaoNullLimpar() {
        assertDoesNotThrow(() -> cacheService.limparSessao(null));
    }
}
