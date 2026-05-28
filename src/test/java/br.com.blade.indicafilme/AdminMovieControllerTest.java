package br.com.blade.indicafilme;

import br.com.blade.indicafilme.controller.AdminMovieController;
import br.com.blade.indicafilme.dto.DivinePatchDto;
import br.com.blade.indicafilme.dto.MovieDto;
import br.com.blade.indicafilme.dto.MovieRequestDto;
import br.com.blade.indicafilme.dto.StatusPatchDto;
import br.com.blade.indicafilme.exception.NotFoundException;
import br.com.blade.indicafilme.model.Movie;
import br.com.blade.indicafilme.model.StatusFilme;
import br.com.blade.indicafilme.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AdminMovieControllerTest {
    @Mock
    private MovieService movieService;

    @InjectMocks
    private AdminMovieController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ========== Helper Methods ==========

    private MovieRequestDto criarDtoValido() {
        MovieRequestDto dto = new MovieRequestDto();
        dto.setTitulo("Filme Admin Test");
        dto.setAutor("Diretor Test");
        dto.setAnolancamento(2020);
        dto.setDuracao(120);
        dto.setSinopse("Sinopse teste");
        return dto;
    }

    private Movie criarMovieMock(String id, String titulo, StatusFilme status) {
        Movie movie = mock(Movie.class);
        when(movie.getId()).thenReturn(id);
        when(movie.getTitulo()).thenReturn(titulo);
        when(movie.getStatus()).thenReturn(status);
        return movie;
    }

    // ========== Testes de criarFilme ==========

    @Test
    void criarFilme_sucesso() {
        MovieRequestDto dto = criarDtoValido();
        Movie movie = criarMovieMock("id1", "Filme Admin Test", StatusFilme.AGUARDANDO);
        
        when(movieService.save(any())).thenReturn(movie);
        
        ResponseEntity<MovieDto> response = controller.criarFilme(dto);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("id1", response.getBody().getId());
        verify(movieService, times(1)).save(any());
    }

    @Test
    void criarFilme_statusPadraoAGUARDANDO() {
        MovieRequestDto dto = criarDtoValido();
        Movie movie = criarMovieMock("id1", "Filme", StatusFilme.AGUARDANDO);
        
        when(movieService.save(any())).thenReturn(movie);
        
        ResponseEntity<MovieDto> response = controller.criarFilme(dto);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(StatusFilme.AGUARDANDO, response.getBody().getId() != null ? StatusFilme.AGUARDANDO : null);
    }

    @Test
    void criarFilme_comTodosOsCampos() {
        MovieRequestDto dto = criarDtoValido();
        dto.setNotaDivina(8.5);
        dto.setNotaPublico(7.5);
        dto.setMotivoRecomendacao("Excelente filme");
        
        Movie movie = criarMovieMock("id1", "Filme", StatusFilme.AGUARDANDO);
        when(movieService.save(any())).thenReturn(movie);
        
        ResponseEntity<MovieDto> response = controller.criarFilme(dto);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void criarFilme_retorna201Created() {
        MovieRequestDto dto = criarDtoValido();
        Movie movie = criarMovieMock("novo-id", "Novo Filme", StatusFilme.AGUARDANDO);
        
        when(movieService.save(any())).thenReturn(movie);
        
        ResponseEntity<MovieDto> response = controller.criarFilme(dto);
        
        assertEquals(201, response.getStatusCodeValue());
    }

    // ========== Testes de atualizarFilme ==========

    @Test
    void atualizarFilme_sucesso() {
        MovieRequestDto dto = criarDtoValido();
        Movie existente = criarMovieMock("id1", "Antigo", StatusFilme.AGUARDANDO);
        Movie atualizado = criarMovieMock("id1", "Filme Admin Test", StatusFilme.AGUARDANDO);
        
        when(movieService.findById("id1")).thenReturn(Optional.of(existente));
        when(movieService.save(any())).thenReturn(atualizado);
        
        ResponseEntity<MovieDto> response = controller.atualizarFilme("id1", dto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void atualizarFilme_naoEncontrado() {
        MovieRequestDto dto = criarDtoValido();
        when(movieService.findById("id-inexistente")).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> 
            controller.atualizarFilme("id-inexistente", dto)
        );
    }

    @Test
    void atualizarFilme_mantemId() {
        MovieRequestDto dto = criarDtoValido();
        Movie existente = criarMovieMock("id1", "Antigo", StatusFilme.AGUARDANDO);
        Movie atualizado = criarMovieMock("id1", "Novo", StatusFilme.AGUARDANDO);
        
        when(movieService.findById("id1")).thenReturn(Optional.of(existente));
        when(movieService.save(any())).thenReturn(atualizado);
        
        ResponseEntity<MovieDto> response = controller.atualizarFilme("id1", dto);
        
        assertEquals("id1", response.getBody().getId());
    }

    @Test
    void atualizarFilme_retorna200Ok() {
        MovieRequestDto dto = criarDtoValido();
        Movie existente = criarMovieMock("id1", "Antigo", StatusFilme.AGUARDANDO);
        Movie atualizado = criarMovieMock("id1", "Novo", StatusFilme.AGUARDANDO);
        
        when(movieService.findById("id1")).thenReturn(Optional.of(existente));
        when(movieService.save(any())).thenReturn(atualizado);
        
        ResponseEntity<MovieDto> response = controller.atualizarFilme("id1", dto);
        
        assertEquals(200, response.getStatusCodeValue());
    }

    // ========== Testes de atualizarDivino (PATCH /divine) ==========

    @Test
    void atualizarDivino_sucesso() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setNotaDivina(9.0);
        dto.setMotivoRecomendacao("Filme excepcional");
        
        Movie movie = criarMovieMock("id1", "Filme", StatusFilme.ATIVO);
        when(movieService.findById("id1")).thenReturn(Optional.of(movie));
        when(movieService.save(any())).thenReturn(movie);
        
        ResponseEntity<MovieDto> response = controller.atualizarDivino("id1", dto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void atualizarDivino_filmeNaoEncontrado() {
        DivinePatchDto dto = new DivinePatchDto();
        when(movieService.findById("id-inexistente")).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> 
            controller.atualizarDivino("id-inexistente", dto)
        );
    }

    @Test
    void atualizarDivino_apenasNotaDivina() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setNotaDivina(8.5);
        dto.setMotivoRecomendacao(null);
        
        Movie movie = criarMovieMock("id1", "Filme", StatusFilme.ATIVO);
        when(movieService.findById("id1")).thenReturn(Optional.of(movie));
        when(movieService.save(any())).thenReturn(movie);
        
        ResponseEntity<MovieDto> response = controller.atualizarDivino("id1", dto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void atualizarDivino_apenasMotivo() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setNotaDivina(null);
        dto.setMotivoRecomendacao("Motivo incrível");
        
        Movie movie = criarMovieMock("id1", "Filme", StatusFilme.ATIVO);
        when(movieService.findById("id1")).thenReturn(Optional.of(movie));
        when(movieService.save(any())).thenReturn(movie);
        
        ResponseEntity<MovieDto> response = controller.atualizarDivino("id1", dto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void atualizarDivino_nenhumpFieldoAlterado() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setNotaDivina(null);
        dto.setMotivoRecomendacao(null);
        
        Movie movie = criarMovieMock("id1", "Filme", StatusFilme.ATIVO);
        when(movieService.findById("id1")).thenReturn(Optional.of(movie));
        when(movieService.save(any())).thenReturn(movie);
        
        ResponseEntity<MovieDto> response = controller.atualizarDivino("id1", dto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void atualizarDivino_retorna200Ok() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setNotaDivina(7.0);
        
        Movie movie = criarMovieMock("id1", "Filme", StatusFilme.ATIVO);
        when(movieService.findById("id1")).thenReturn(Optional.of(movie));
        when(movieService.save(any())).thenReturn(movie);
        
        ResponseEntity<MovieDto> response = controller.atualizarDivino("id1", dto);
        
        assertEquals(200, response.getStatusCodeValue());
    }

    // ========== Testes de atualizarStatus (PATCH /status) ==========

    @Test
    void atualizarStatus_paraATIVO() {
        StatusPatchDto dto = new StatusPatchDto(StatusFilme.ATIVO);
        
        Movie movie = criarMovieMock("id1", "Filme", StatusFilme.AGUARDANDO);
        when(movieService.atualizarStatus("id1", StatusFilme.ATIVO)).thenReturn(movie);
        
        ResponseEntity<MovieDto> response = controller.atualizarStatus("id1", dto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void atualizarStatus_paraERRO() {
        StatusPatchDto dto = new StatusPatchDto(StatusFilme.ERRO);
        
        Movie movie = criarMovieMock("id1", "Filme", StatusFilme.ATIVO);
        when(movieService.atualizarStatus("id1", StatusFilme.ERRO)).thenReturn(movie);
        
        ResponseEntity<MovieDto> response = controller.atualizarStatus("id1", dto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void atualizarStatus_paraAGUARDANDO() {
        StatusPatchDto dto = new StatusPatchDto(StatusFilme.AGUARDANDO);
        
        Movie movie = criarMovieMock("id1", "Filme", StatusFilme.ATIVO);
        when(movieService.atualizarStatus("id1", StatusFilme.AGUARDANDO)).thenReturn(movie);
        
        ResponseEntity<MovieDto> response = controller.atualizarStatus("id1", dto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void atualizarStatus_filmeNaoEncontrado() {
        StatusPatchDto dto = new StatusPatchDto(StatusFilme.ATIVO);
        when(movieService.atualizarStatus("id-inexistente", StatusFilme.ATIVO))
                .thenThrow(new NotFoundException("Filme não encontrado"));
        
        assertThrows(NotFoundException.class, () -> 
            controller.atualizarStatus("id-inexistente", dto)
        );
    }

    @Test
    void atualizarStatus_retorna200Ok() {
        StatusPatchDto dto = new StatusPatchDto(StatusFilme.ATIVO);
        
        Movie movie = criarMovieMock("id1", "Filme", StatusFilme.AGUARDANDO);
        when(movieService.atualizarStatus("id1", StatusFilme.ATIVO)).thenReturn(movie);
        
        ResponseEntity<MovieDto> response = controller.atualizarStatus("id1", dto);
        
        assertEquals(200, response.getStatusCodeValue());
    }

    // ========== Testes de deletarFilme ==========

    @Test
    void deletarFilme_sucesso() {
        doNothing().when(movieService).deleteById("id1");
        
        ResponseEntity<Void> response = controller.deletarFilme("id1");
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(movieService, times(1)).deleteById("id1");
    }

    @Test
    void deletarFilme_retorna204NoContent() {
        doNothing().when(movieService).deleteById("id1");
        
        ResponseEntity<Void> response = controller.deletarFilme("id1");
        
        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void deletarFilme_verificaChamada() {
        doNothing().when(movieService).deleteById("id-teste");
        
        controller.deletarFilme("id-teste");
        
        verify(movieService, times(1)).deleteById("id-teste");
    }

    // ========== Testes de Edge Cases ==========

    @Test
    void criarFilme_tituloMuitoLongo() {
        MovieRequestDto dto = criarDtoValido();
        dto.setTitulo("A".repeat(1000));
        
        Movie movie = criarMovieMock("id1", dto.getTitulo(), StatusFilme.AGUARDANDO);
        when(movieService.save(any())).thenReturn(movie);
        
        ResponseEntity<MovieDto> response = controller.criarFilme(dto);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void atualizarFilme_multiplasVezes() {
        MovieRequestDto dto = criarDtoValido();
        Movie existente = criarMovieMock("id1", "Original", StatusFilme.AGUARDANDO);
        Movie atualizado1 = criarMovieMock("id1", "Atualizado1", StatusFilme.AGUARDANDO);
        Movie atualizado2 = criarMovieMock("id1", "Atualizado2", StatusFilme.AGUARDANDO);
        
        when(movieService.findById("id1")).thenReturn(Optional.of(existente));
        when(movieService.save(any())).thenReturn(atualizado1);
        
        ResponseEntity<MovieDto> response1 = controller.atualizarFilme("id1", dto);
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        
        when(movieService.save(any())).thenReturn(atualizado2);
        ResponseEntity<MovieDto> response2 = controller.atualizarFilme("id1", dto);
        assertEquals(HttpStatus.OK, response2.getStatusCode());
    }

    @Test
    void atualizarStatus_transicoesMitiuples() {
        StatusFilme[] statuses = {StatusFilme.ATIVO, StatusFilme.AGUARDANDO, StatusFilme.ERRO, StatusFilme.ATIVO};
        Movie movie = criarMovieMock("id1", "Filme", StatusFilme.ATIVO);
        
        for (StatusFilme status : statuses) {
            when(movieService.atualizarStatus("id1", status)).thenReturn(movie);
            StatusPatchDto dto = new StatusPatchDto(status);
            ResponseEntity<MovieDto> response = controller.atualizarStatus("id1", dto);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Test
    void deletarFilme_multiplasVezes() {
        doNothing().when(movieService).deleteById(anyString());
        
        controller.deletarFilme("id1");
        controller.deletarFilme("id2");
        controller.deletarFilme("id3");
        
        verify(movieService, times(3)).deleteById(anyString());
    }

    @Test
    void atualizarDivino_notaDivinaZero() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setNotaDivina(0.0);
        
        Movie movie = criarMovieMock("id1", "Filme", StatusFilme.ATIVO);
        when(movieService.findById("id1")).thenReturn(Optional.of(movie));
        when(movieService.save(any())).thenReturn(movie);
        
        ResponseEntity<MovieDto> response = controller.atualizarDivino("id1", dto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void atualizarDivino_notaDivinaDez() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setNotaDivina(10.0);
        
        Movie movie = criarMovieMock("id1", "Filme", StatusFilme.ATIVO);
        when(movieService.findById("id1")).thenReturn(Optional.of(movie));
        when(movieService.save(any())).thenReturn(movie);
        
        ResponseEntity<MovieDto> response = controller.atualizarDivino("id1", dto);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}



