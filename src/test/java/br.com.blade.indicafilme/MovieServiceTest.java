package br.com.blade.indicafilme;

import br.com.blade.indicafilme.dto.MovieSearchCriteria;
import br.com.blade.indicafilme.exception.BadRequestException;
import br.com.blade.indicafilme.exception.NotFoundException;
import br.com.blade.indicafilme.model.Movie;
import br.com.blade.indicafilme.model.StatusFilme;
import br.com.blade.indicafilme.repository.MovieRepository;
import br.com.blade.indicafilme.service.MovieService;
import br.com.blade.indicafilme.service.TmdbService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class MovieServiceTest {
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private TmdbService tmdbService;
    @InjectMocks
    private MovieService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ========== Testes de filterMovies ==========

    @Test
    void filterMovies_semFiltro() {
        MovieSearchCriteria criteria = new MovieSearchCriteria(null, null, null);
        assertThrows(BadRequestException.class, () -> service.filterMovies(criteria));
    }

    @Test
    void filterMovies_comFiltroGenero() {
        Movie movie = createMovieMock("id1", "Filme Ação", 120, 2020, List.of("Ação"), StatusFilme.ATIVO);
        when(movieRepository.findByStatus(StatusFilme.ATIVO)).thenReturn(List.of(movie));
        
        MovieSearchCriteria criteria = new MovieSearchCriteria(List.of("Ação"), null, null);
        List<Movie> result = service.filterMovies(criteria);
        assertFalse(result.isEmpty());
    }

    @Test
    void filterMovies_comFiltroDuracao_CURTA() {
        Movie movie = createMovieMock("id1", "Curta", 45, 2020, null, StatusFilme.ATIVO);
        when(movieRepository.findByStatus(StatusFilme.ATIVO)).thenReturn(List.of(movie));
        
        MovieSearchCriteria criteria = new MovieSearchCriteria(null, List.of("CURTA"), null);
        List<Movie> result = service.filterMovies(criteria);
        assertFalse(result.isEmpty());
    }

    @Test
    void filterMovies_comFiltroDuracao_MEDIA() {
        Movie movie = createMovieMock("id1", "Média", 90, 2020, null, StatusFilme.ATIVO);
        when(movieRepository.findByStatus(StatusFilme.ATIVO)).thenReturn(List.of(movie));
        
        MovieSearchCriteria criteria = new MovieSearchCriteria(null, List.of("MEDIA"), null);
        List<Movie> result = service.filterMovies(criteria);
        assertFalse(result.isEmpty());
    }

    @Test
    void filterMovies_comFiltroDuracao_LONGA() {
        Movie movie = createMovieMock("id1", "Longa", 180, 2020, null, StatusFilme.ATIVO);
        when(movieRepository.findByStatus(StatusFilme.ATIVO)).thenReturn(List.of(movie));
        
        MovieSearchCriteria criteria = new MovieSearchCriteria(null, List.of("LONGA"), null);
        List<Movie> result = service.filterMovies(criteria);
        assertFalse(result.isEmpty());
    }

    @Test
    void filterMovies_comFiltroDecada() {
        Movie movie = createMovieMock("id1", "Filme 90", 120, 1995, null, StatusFilme.ATIVO);
        when(movieRepository.findByStatus(StatusFilme.ATIVO)).thenReturn(List.of(movie));
        
        MovieSearchCriteria criteria = new MovieSearchCriteria(null, null, List.of(1990));
        List<Movie> result = service.filterMovies(criteria);
        assertFalse(result.isEmpty());
    }

    @Test
    void filterMovies_multiplosFiltros() {
        Movie movie = createMovieMock("id1", "Filme", 90, 2020, List.of("Drama"), StatusFilme.ATIVO);
        when(movieRepository.findByStatus(StatusFilme.ATIVO)).thenReturn(List.of(movie));
        
        MovieSearchCriteria criteria = new MovieSearchCriteria(List.of("Drama"), List.of("MEDIA"), List.of(2020));
        List<Movie> result = service.filterMovies(criteria);
        assertFalse(result.isEmpty());
    }

    @Test
    void filterMovies_naoRetornaFilmesInativos() {
        Movie inativo = createMovieMock("id1", "Inativo", 120, 2020, List.of("Ação"), StatusFilme.AGUARDANDO);
        when(movieRepository.findByStatus(StatusFilme.ATIVO)).thenReturn(List.of());
        
        MovieSearchCriteria criteria = new MovieSearchCriteria(List.of("Ação"), null, null);
        List<Movie> result = service.filterMovies(criteria);
        assertTrue(result.isEmpty());
    }

    @Test
    void filterMovies_duraçao60minutos() {
        Movie movie = createMovieMock("id1", "Exato", 60, 2020, null, StatusFilme.ATIVO);
        when(movieRepository.findByStatus(StatusFilme.ATIVO)).thenReturn(List.of(movie));
        
        MovieSearchCriteria criteria = new MovieSearchCriteria(null, List.of("CURTA"), null);
        List<Movie> result = service.filterMovies(criteria);
        assertFalse(result.isEmpty());
    }

    @Test
    void filterMovies_duraçao61minutos() {
        Movie movie = createMovieMock("id1", "Início Média", 61, 2020, null, StatusFilme.ATIVO);
        when(movieRepository.findByStatus(StatusFilme.ATIVO)).thenReturn(List.of(movie));
        
        MovieSearchCriteria criteria = new MovieSearchCriteria(null, List.of("MEDIA"), null);
        List<Movie> result = service.filterMovies(criteria);
        assertFalse(result.isEmpty());
    }

    @Test
    void filterMovies_duraçao110minutos() {
        Movie movie = createMovieMock("id1", "Final Média", 110, 2020, null, StatusFilme.ATIVO);
        when(movieRepository.findByStatus(StatusFilme.ATIVO)).thenReturn(List.of(movie));
        
        MovieSearchCriteria criteria = new MovieSearchCriteria(null, List.of("MEDIA"), null);
        List<Movie> result = service.filterMovies(criteria);
        assertFalse(result.isEmpty());
    }

    @Test
    void filterMovies_duraçao111minutos() {
        Movie movie = createMovieMock("id1", "Longa", 111, 2020, null, StatusFilme.ATIVO);
        when(movieRepository.findByStatus(StatusFilme.ATIVO)).thenReturn(List.of(movie));
        
        MovieSearchCriteria criteria = new MovieSearchCriteria(null, List.of("LONGA"), null);
        List<Movie> result = service.filterMovies(criteria);
        assertFalse(result.isEmpty());
    }

    @Test
    void filterMovies_filmeComGenerosNull() {
        Movie movie = createMovieMock("id1", "Sem Gênero", 120, 2020, null, StatusFilme.ATIVO);
        when(movieRepository.findByStatus(StatusFilme.ATIVO)).thenReturn(List.of(movie));
        
        MovieSearchCriteria criteria = new MovieSearchCriteria(List.of("Ação"), null, null);
        List<Movie> result = service.filterMovies(criteria);
        assertTrue(result.isEmpty());
    }

    @Test
    void filterMovies_filmeComDuracaoNull() {
        Movie movie = createMovieMock("id1", "Sem Duração", null, 2020, null, StatusFilme.ATIVO);
        when(movieRepository.findByStatus(StatusFilme.ATIVO)).thenReturn(List.of(movie));
        
        MovieSearchCriteria criteria = new MovieSearchCriteria(null, List.of("MEDIA"), null);
        List<Movie> result = service.filterMovies(criteria);
        assertTrue(result.isEmpty());
    }

    @Test
    void filterMovies_filmeComAnoNull() {
        Movie movie = createMovieMock("id1", "Sem Ano", 120, null, null, StatusFilme.ATIVO);
        when(movieRepository.findByStatus(StatusFilme.ATIVO)).thenReturn(List.of(movie));
        
        MovieSearchCriteria criteria = new MovieSearchCriteria(null, null, List.of(2020));
        List<Movie> result = service.filterMovies(criteria);
        assertTrue(result.isEmpty());
    }

    // ========== Testes de pickRandom ==========

    @Test
    void pickRandom_listaVazia() {
        assertTrue(service.pickRandom(List.of(), null).isEmpty());
    }

    @Test
    void pickRandom_listaNula() {
        assertTrue(service.pickRandom(null, null).isEmpty());
    }

    @Test
    void pickRandom_umFilme() {
        Movie movie = createMovieMock("id1", "Único", 120, 2020, null, StatusFilme.ATIVO);
        Optional<Movie> result = service.pickRandom(List.of(movie), null);
        assertTrue(result.isPresent());
        assertEquals("id1", result.get().getId());
    }

    @Test
    void pickRandom_multiplasVezesRetornaAlgumFilme() {
        Movie m1 = createMovieMock("id1", "F1", 120, 2020, null, StatusFilme.ATIVO);
        Movie m2 = createMovieMock("id2", "F2", 120, 2020, null, StatusFilme.ATIVO);
        Movie m3 = createMovieMock("id3", "F3", 120, 2020, null, StatusFilme.ATIVO);
        List<Movie> movies = List.of(m1, m2, m3);
        
        for (int i = 0; i < 10; i++) {
            Optional<Movie> result = service.pickRandom(movies, null);
            assertTrue(result.isPresent());
            assertTrue(List.of("id1", "id2", "id3").contains(result.get().getId()));
        }
    }

    @Test
    void pickRandom_excluiFilmeEspecifico() {
        Movie m1 = createMovieMock("id1", "F1", 120, 2020, null, StatusFilme.ATIVO);
        Movie m2 = createMovieMock("id2", "F2", 120, 2020, null, StatusFilme.ATIVO);
        List<Movie> movies = List.of(m1, m2);
        
        Optional<Movie> result = service.pickRandom(movies, "id1");
        assertTrue(result.isPresent());
        if (movies.size() > 1) {
            assertNotEquals("id1", result.get().getId());
        }
    }

    @Test
    void pickRandom_excluiQuandoTodoSoMesmo() {
        Movie m1 = createMovieMock("id1", "F1", 120, 2020, null, StatusFilme.ATIVO);
        List<Movie> movies = List.of(m1);
        
        Optional<Movie> result = service.pickRandom(movies, "id1");
        assertTrue(result.isPresent());
        assertEquals("id1", result.get().getId());
    }

    @Test
    void pickRandom_excludeIdNull() {
        Movie movie = createMovieMock("id1", "F1", 120, 2020, null, StatusFilme.ATIVO);
        Optional<Movie> result = service.pickRandom(List.of(movie), null);
        assertTrue(result.isPresent());
    }

    @Test
    void pickRandom_excludeIdVazio() {
        Movie movie = createMovieMock("id1", "F1", 120, 2020, null, StatusFilme.ATIVO);
        Optional<Movie> result = service.pickRandom(List.of(movie), "");
        assertTrue(result.isPresent());
    }

    @Test
    void pickRandom_semExcluirId() {
        Movie m1 = createMovieMock("id1", "F1", 120, 2020, null, StatusFilme.ATIVO);
        Movie m2 = createMovieMock("id2", "F2", 120, 2020, null, StatusFilme.ATIVO);
        List<Movie> movies = List.of(m1, m2);
        
        Optional<Movie> result = service.pickRandom(movies);
        assertTrue(result.isPresent());
    }

    // ========== Testes de findById e Enriquecimento TMDB ==========

    @Test
    void findById_sucesso() {
        Movie movie = createMovieMock("id1", "Filme", 120, 2020, null, StatusFilme.ATIVO);
        when(movieRepository.findById("id1")).thenReturn(Optional.of(movie));
        when(tmdbService.buscarNotaPublico(anyString(), any())).thenReturn(Optional.empty());
        
        Optional<Movie> result = service.findById("id1");
        assertTrue(result.isPresent());
    }

    @Test
    void findById_naoEncontrado() {
        when(movieRepository.findById("id1")).thenReturn(Optional.empty());
        Optional<Movie> result = service.findById("id1");
        assertTrue(result.isEmpty());
    }

    @Test
    void findById_enriqueceComTmdb() {
        Movie movie = createMovieMock("id1", "Filme", 120, 2020, null, StatusFilme.ATIVO);
        when(movie.getNotaPublico()).thenReturn(null);
        when(movieRepository.findById("id1")).thenReturn(Optional.of(movie));
        when(tmdbService.buscarNotaPublico("Filme", 2020)).thenReturn(Optional.of(8.5));
        
        Optional<Movie> result = service.findById("id1");
        assertTrue(result.isPresent());
        verify(tmdbService, times(1)).buscarNotaPublico("Filme", 2020);
    }

    @Test
    void findByTitle_sucesso() {
        Movie movie = createMovieMock("id1", "Filme Específico", 120, 2020, null, StatusFilme.ATIVO);
        when(movieRepository.findByTituloIgnoreCase("Filme Específico")).thenReturn(Optional.of(movie));
        
        Optional<Movie> result = service.findByTitle("Filme Específico");
        assertTrue(result.isPresent());
    }

    @Test
    void findByTitle_naoEncontrado() {
        when(movieRepository.findByTituloIgnoreCase("Inexistente")).thenReturn(Optional.empty());
        Optional<Movie> result = service.findByTitle("Inexistente");
        assertTrue(result.isEmpty());
    }

    @Test
    void enriquecerComTmdb_notaPublicoJaPreenchida() {
        Movie movie = createMovieMock("id1", "Filme", 120, 2020, null, StatusFilme.ATIVO);
        when(movie.getNotaPublico()).thenReturn(7.0);
        
        service.enriquecerComTmdb(movie);
        verify(tmdbService, never()).buscarNotaPublico(anyString(), any());
    }

    @Test
    void enriquecerComTmdb_notaPublicoNulaEncontradaTmdb() {
        Movie movie = createMovieMock("id1", "Filme", 120, 2020, null, StatusFilme.ATIVO);
        when(movie.getNotaPublico()).thenReturn(null);
        when(movie.getTitulo()).thenReturn("Filme");
        when(movie.getAnoLancamento()).thenReturn(2020);
        when(tmdbService.buscarNotaPublico("Filme", 2020)).thenReturn(Optional.of(8.5));
        
        service.enriquecerComTmdb(movie);
        verify(movie, times(1)).setNotaPublico(8.5);
    }

    @Test
    void enriquecerComTmdb_titulonulo() {
        Movie movie = createMovieMock("id1", null, 120, 2020, null, StatusFilme.ATIVO);
        when(movie.getTitulo()).thenReturn(null);
        
        Movie result = service.enriquecerComTmdb(movie);
        assertNotNull(result);
        verify(tmdbService, never()).buscarNotaPublico(anyString(), any());
    }

    // ========== Testes de save ==========

    @Test
    void save_filmeValido() {
        Movie movie = createMovieMock("id1", "Filme", 120, 2020, null, StatusFilme.ATIVO);
        when(movieRepository.save(any())).thenReturn(movie);
        
        Movie result = service.save(movie);
        assertNotNull(result);
        verify(movieRepository, times(1)).save(movie);
    }

    @Test
    void save_retornaFilmeSalvo() {
        Movie movie = createMovieMock("id1", "Filme", 120, 2020, null, StatusFilme.ATIVO);
        when(movieRepository.save(any())).thenReturn(movie);
        
        Movie result = service.save(movie);
        assertEquals("id1", result.getId());
        assertEquals("Filme", result.getTitulo());
    }

    // ========== Testes de atualizarStatus ==========

    @Test
    void atualizarStatus_sucesso() {
        Movie movie = createMovieMock("id1", "Filme", 120, 2020, null, StatusFilme.AGUARDANDO);
        when(movieRepository.findById("id1")).thenReturn(Optional.of(movie));
        when(movieRepository.save(any())).thenReturn(movie);
        
        Movie result = service.atualizarStatus("id1", StatusFilme.ATIVO);
        assertNotNull(result);
        verify(movie, times(1)).setStatus(StatusFilme.ATIVO);
    }

    @Test
    void atualizarStatus_filmeNaoEncontrado() {
        when(movieRepository.findById("id1")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.atualizarStatus("id1", StatusFilme.ATIVO));
    }

    @Test
    void atualizarStatus_pareaATIVO() {
        Movie movie = createMovieMock("id1", "Filme", 120, 2020, null, StatusFilme.AGUARDANDO);
        when(movieRepository.findById("id1")).thenReturn(Optional.of(movie));
        when(movieRepository.save(any())).thenReturn(movie);
        
        service.atualizarStatus("id1", StatusFilme.ATIVO);
        verify(movie, times(1)).setStatus(StatusFilme.ATIVO);
    }

    @Test
    void atualizarStatus_paraERRO() {
        Movie movie = createMovieMock("id1", "Filme", 120, 2020, null, StatusFilme.ATIVO);
        when(movieRepository.findById("id1")).thenReturn(Optional.of(movie));
        when(movieRepository.save(any())).thenReturn(movie);
        
        service.atualizarStatus("id1", StatusFilme.ERRO);
        verify(movie, times(1)).setStatus(StatusFilme.ERRO);
    }

    // ========== Testes de deleteById ==========

    @Test
    void deleteById_chamRepositorio() {
        doNothing().when(movieRepository).deleteById("id1");
        service.deleteById("id1");
        verify(movieRepository, times(1)).deleteById("id1");
    }

    // ========== Helper Methods ==========

    private Movie createMovieMock(String id, String titulo, Integer duracao, Integer ano, List<String> generos, StatusFilme status) {
        Movie movie = mock(Movie.class);
        when(movie.getId()).thenReturn(id);
        when(movie.getTitulo()).thenReturn(titulo);
        when(movie.getDuracao()).thenReturn(duracao);
        when(movie.getAnoLancamento()).thenReturn(ano);
        when(movie.getGeneros()).thenReturn(generos);
        when(movie.getStatus()).thenReturn(status);
        return movie;
    }
}
