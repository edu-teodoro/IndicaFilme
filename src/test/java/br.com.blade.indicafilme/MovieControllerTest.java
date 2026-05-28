package br.com.blade.indicafilme;

import br.com.blade.indicafilme.controller.MovieController;
import br.com.blade.indicafilme.dto.MovieDto;
import br.com.blade.indicafilme.dto.MovieSearchCriteria;
import br.com.blade.indicafilme.exception.BadRequestException;
import br.com.blade.indicafilme.exception.NotFoundException;
import br.com.blade.indicafilme.model.Movie;
import br.com.blade.indicafilme.model.StatusFilme;
import br.com.blade.indicafilme.service.MovieCacheService;
import br.com.blade.indicafilme.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MovieControllerTest {
    @Mock
    private MovieService movieService;
    @Mock
    private MovieCacheService cacheService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;

    @InjectMocks
    private MovieController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(session.getId()).thenReturn("sessao1");
    }

    // ========== Helper Method ==========
    
    private Movie createMovieMock(String id, String titulo) {
        Movie movie = mock(Movie.class);
        when(movie.getId()).thenReturn(id);
        when(movie.getTitulo()).thenReturn(titulo);
        when(movie.getStatus()).thenReturn(StatusFilme.ATIVO);
        return movie;
    }

    // ========== Testes de sortearFilme ==========

    @Test
    void sortearFilme_listaVazia() {
        when(movieService.filterMovies(any())).thenReturn(Collections.emptyList());
        ResponseEntity<?> response = controller.sortearFilme(null, null, null, request);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Desculpe"));
    }

    @Test
    void sortearFilme_sucesso() {
        Movie movie = createMovieMock("id1", "Filme Teste");
        when(movieService.filterMovies(any())).thenReturn(List.of(movie));
        when(cacheService.getUltimoFilmeId(anyString())).thenReturn(null);
        when(movieService.pickRandom(anyList(), any())).thenReturn(Optional.of(movie));
        when(movieService.enriquecerComTmdb(any())).thenReturn(movie);
        
        ResponseEntity<?> response = controller.sortearFilme(null, null, null, request);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof MovieDto);
    }

    @Test
    void sortearFilme_comCacheSession() {
        Movie movie = createMovieMock("id1", "Filme");
        when(movieService.filterMovies(any())).thenReturn(List.of(movie));
        when(cacheService.getUltimoFilmeId("sessao1")).thenReturn("id-anterior");
        when(movieService.pickRandom(anyList(), eq("id-anterior"))).thenReturn(Optional.of(movie));
        when(movieService.enriquecerComTmdb(any())).thenReturn(movie);
        
        ResponseEntity<?> response = controller.sortearFilme(null, null, null, request);
        assertEquals(200, response.getStatusCodeValue());
        verify(cacheService, times(1)).registrarUltimoFilme("sessao1", "id1");
    }

    @Test
    void sortearFilme_comFiltroGenero() {
        Movie movie = createMovieMock("id1", "Ação");
        when(movieService.filterMovies(any())).thenReturn(List.of(movie));
        when(cacheService.getUltimoFilmeId(anyString())).thenReturn(null);
        when(movieService.pickRandom(anyList(), any())).thenReturn(Optional.of(movie));
        when(movieService.enriquecerComTmdb(any())).thenReturn(movie);
        
        ResponseEntity<?> response = controller.sortearFilme(List.of("Ação"), null, null, request);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void sortearFilme_comFiltroDuracao() {
        Movie movie = createMovieMock("id1", "Curta");
        when(movieService.filterMovies(any())).thenReturn(List.of(movie));
        when(cacheService.getUltimoFilmeId(anyString())).thenReturn(null);
        when(movieService.pickRandom(anyList(), any())).thenReturn(Optional.of(movie));
        when(movieService.enriquecerComTmdb(any())).thenReturn(movie);
        
        ResponseEntity<?> response = controller.sortearFilme(null, List.of("CURTA"), null, request);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void sortearFilme_comFiltroDecada() {
        Movie movie = createMovieMock("id1", "Antigas");
        when(movieService.filterMovies(any())).thenReturn(List.of(movie));
        when(cacheService.getUltimoFilmeId(anyString())).thenReturn(null);
        when(movieService.pickRandom(anyList(), any())).thenReturn(Optional.of(movie));
        when(movieService.enriquecerComTmdb(any())).thenReturn(movie);
        
        ResponseEntity<?> response = controller.sortearFilme(null, null, List.of(1990), request);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void sortearFilme_semaFiltros() {
        when(movieService.filterMovies(any())).thenThrow(new BadRequestException("Nenhum filtro"));
        
        assertThrows(BadRequestException.class, () -> 
            controller.sortearFilme(null, null, null, request)
        );
    }

    @Test
    void sortearFilme_decadaInvalida1() {
        ResponseEntity<?> response = controller.sortearFilme(null, null, List.of(1887), request);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void sortearFilme_decadaInvalida2() {
        ResponseEntity<?> response = controller.sortearFilme(null, null, List.of(2101), request);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void sortearFilme_registraNoCache() {
        Movie movie = createMovieMock("id-novo", "Filme");
        when(movieService.filterMovies(any())).thenReturn(List.of(movie));
        when(cacheService.getUltimoFilmeId("sessao1")).thenReturn(null);
        when(movieService.pickRandom(anyList(), any())).thenReturn(Optional.of(movie));
        when(movieService.enriquecerComTmdb(any())).thenReturn(movie);
        
        controller.sortearFilme(null, null, null, request);
        verify(cacheService, times(1)).registrarUltimoFilme("sessao1", "id-novo");
    }

    // ========== Testes de listarFilmes ==========

    @Test
    void listarFilmes_listaVazia() {
        when(movieService.filterMovies(any())).thenReturn(Collections.emptyList());
        ResponseEntity<?> response = controller.listarFilmes(null, null, null);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Desculpe"));
    }

    @Test
    void listarFilmes_sucesso() {
        Movie movie = createMovieMock("id1", "Filme");
        when(movieService.filterMovies(any())).thenReturn(List.of(movie));
        
        ResponseEntity<?> response = controller.listarFilmes(null, null, null);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof List);
    }

    @Test
    void listarFilmes_multiplasFilmes() {
        Movie m1 = createMovieMock("id1", "Filme1");
        Movie m2 = createMovieMock("id2", "Filme2");
        Movie m3 = createMovieMock("id3", "Filme3");
        when(movieService.filterMovies(any())).thenReturn(List.of(m1, m2, m3));
        
        ResponseEntity<?> response = controller.listarFilmes(null, null, null);
        assertEquals(200, response.getStatusCodeValue());
        List<?> body = (List<?>) response.getBody();
        assertEquals(3, body.size());
    }

    @Test
    void listarFilmes_comFiltro() {
        Movie movie = createMovieMock("id1", "Ação");
        when(movieService.filterMovies(any())).thenReturn(List.of(movie));
        
        ResponseEntity<?> response = controller.listarFilmes(List.of("Ação"), null, null);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void listarFilmes_semFiltro() {
        when(movieService.filterMovies(any())).thenThrow(new BadRequestException("Nenhum filtro"));
        
        assertThrows(BadRequestException.class, () -> 
            controller.listarFilmes(null, null, null)
        );
    }

    // ========== Testes de buscarPorId ==========

    @Test
    void buscarPorId_sucesso() {
        Movie movie = createMovieMock("id1", "Filme Teste");
        when(movieService.findById("id1")).thenReturn(Optional.of(movie));
        
        ResponseEntity<MovieDto> response = controller.buscarPorId("id1");
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    void buscarPorId_naoEncontrado() {
        when(movieService.findById("id1")).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> controller.buscarPorId("id1"));
    }

    @Test
    void buscarPorId_multiplasConsultas() {
        Movie m1 = createMovieMock("id1", "Filme1");
        Movie m2 = createMovieMock("id2", "Filme2");
        when(movieService.findById("id1")).thenReturn(Optional.of(m1));
        when(movieService.findById("id2")).thenReturn(Optional.of(m2));
        
        ResponseEntity<MovieDto> response1 = controller.buscarPorId("id1");
        ResponseEntity<MovieDto> response2 = controller.buscarPorId("id2");
        
        assertEquals(200, response1.getStatusCodeValue());
        assertEquals(200, response2.getStatusCodeValue());
    }

    // ========== Testes de buscarPorTitulo ==========

    @Test
    void buscarPorTitulo_sucesso() {
        Movie movie = createMovieMock("id1", "Título Específico");
        when(movieService.findByTitle("Título Específico")).thenReturn(Optional.of(movie));
        
        ResponseEntity<MovieDto> response = controller.buscarPorTitulo("Título Específico");
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    void buscarPorTitulo_naoEncontrado() {
        when(movieService.findByTitle("Inexistente")).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> 
            controller.buscarPorTitulo("Inexistente")
        );
    }

    @Test
    void buscarPorTitulo_comEspacos() {
        Movie movie = createMovieMock("id1", "Título Com Espaços");
        when(movieService.findByTitle("Título Com Espaços")).thenReturn(Optional.of(movie));
        
        ResponseEntity<MovieDto> response = controller.buscarPorTitulo("Título Com Espaços");
        assertEquals(200, response.getStatusCodeValue());
    }

    // ========== Testes de Edge Cases ==========

    @Test
    void listarFilmes_umaFilme() {
        Movie movie = createMovieMock("id1", "Um Filme");
        when(movieService.filterMovies(any())).thenReturn(List.of(movie));
        
        ResponseEntity<?> response = controller.listarFilmes(List.of("Drama"), null, null);
        assertEquals(200, response.getStatusCodeValue());
        List<?> body = (List<?>) response.getBody();
        assertEquals(1, body.size());
    }

    @Test
    void sortearFilme_decadaValida1888() {
        Movie movie = createMovieMock("id1", "Antigo");
        when(movieService.filterMovies(any())).thenReturn(List.of(movie));
        when(cacheService.getUltimoFilmeId(anyString())).thenReturn(null);
        when(movieService.pickRandom(anyList(), any())).thenReturn(Optional.of(movie));
        when(movieService.enriquecerComTmdb(any())).thenReturn(movie);
        
        ResponseEntity<?> response = controller.sortearFilme(null, null, List.of(1888), request);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void sortearFilme_decadaValida2100() {
        Movie movie = createMovieMock("id1", "Futuro");
        when(movieService.filterMovies(any())).thenReturn(List.of(movie));
        when(cacheService.getUltimoFilmeId(anyString())).thenReturn(null);
        when(movieService.pickRandom(anyList(), any())).thenReturn(Optional.of(movie));
        when(movieService.enriquecerComTmdb(any())).thenReturn(movie);
        
        ResponseEntity<?> response = controller.sortearFilme(null, null, List.of(2100), request);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void sortearFilme_memoriaEntre3Sessoes() {
        Movie m1 = createMovieMock("id1", "F1");
        Movie m2 = createMovieMock("id2", "F2");
        
        // Sessão 1
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(session.getId()).thenReturn("sessao1");
        when(movieService.filterMovies(any())).thenReturn(List.of(m1));
        when(cacheService.getUltimoFilmeId("sessao1")).thenReturn(null);
        when(movieService.pickRandom(anyList(), any())).thenReturn(Optional.of(m1));
        controller.sortearFilme(null, null, null, request);
        verify(cacheService).registrarUltimoFilme("sessao1", "id1");
    }
}
