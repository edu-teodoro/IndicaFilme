package br.com.blade.indicafilme;

import br.com.blade.indicafilme.dto.MovieRequestDto;
import br.com.blade.indicafilme.model.Platform;
import br.com.blade.indicafilme.model.StatusFilme;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MovieRequestDtoTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ========== Helper Method ==========

    private MovieRequestDto criarDtoValido() {
        MovieRequestDto dto = new MovieRequestDto();
        dto.setTitulo("Filme Test");
        dto.setAutor("Diretor Test");
        dto.setAnoLancamento(2020);
        dto.setDuracao(120);
        dto.setSinopse("Sinopse teste");
        return dto;
    }

    // ========== Testes de Getters e Setters ==========

    @Test
    void getter_setter_titulo() {
        MovieRequestDto dto = new MovieRequestDto();
        dto.setTitulo("Meu Filme");
        assertEquals("Meu Filme", dto.getTitulo());
    }

    @Test
    void getter_setter_autor() {
        MovieRequestDto dto = new MovieRequestDto();
        dto.setAutor("Steven Spielberg");
        assertEquals("Steven Spielberg", dto.getAutor());
    }

    @Test
    void getter_setter_anolancamento() {
        MovieRequestDto dto = new MovieRequestDto();
        dto.setAnoLancamento(1994);
        assertEquals(1994, dto.getAnoLancamento());
    }

    @Test
    void getter_setter_duracao() {
        MovieRequestDto dto = new MovieRequestDto();
        dto.setDuracao(150);
        assertEquals(150, dto.getDuracao());
    }

    @Test
    void getter_setter_generos() {
        MovieRequestDto dto = new MovieRequestDto();
        List<String> generos = List.of("Drama", "Ficção");
        dto.setGeneros(generos);
        assertEquals(generos, dto.getGeneros());
    }

    @Test
    void getter_setter_sinopse() {
        MovieRequestDto dto = new MovieRequestDto();
        dto.setSinopse("Uma história incrível");
        assertEquals("Uma história incrível", dto.getSinopse());
    }

    @Test
    void getter_setter_notaDivina() {
        MovieRequestDto dto = new MovieRequestDto();
        dto.setNotaDivina(8.5);
        assertEquals(8.5, dto.getNotaDivina());
    }

    @Test
    void getter_setter_notaPublico() {
        MovieRequestDto dto = new MovieRequestDto();
        dto.setNotaPublico(7.5);
        assertEquals(7.5, dto.getNotaPublico());
    }

    @Test
    void getter_setter_motivoRecomendacao() {
        MovieRequestDto dto = new MovieRequestDto();
        dto.setMotivoRecomendacao("Excelente filme");
        assertEquals("Excelente filme", dto.getMotivoRecomendacao());
    }

    @Test
    void getter_setter_poster() {
        MovieRequestDto dto = new MovieRequestDto();
        dto.setPoster("https://example.com/poster.jpg");
        assertEquals("https://example.com/poster.jpg", dto.getPoster());
    }

    @Test
    void getter_setter_plataformas() {
        MovieRequestDto dto = new MovieRequestDto();
        Platform netflix = new Platform("Netflix", "https://netflix.com");
        Platform prime = new Platform("Prime Video", "https://prime.com");
        List<Platform> plataformas = List.of(netflix, prime);
        dto.setPlataformas(plataformas);
        assertEquals(plataformas, dto.getPlataformas());
    }

    @Test
    void getter_setter_status() {
        MovieRequestDto dto = new MovieRequestDto();
        dto.setStatus(StatusFilme.ATIVO);
        assertEquals(StatusFilme.ATIVO, dto.getStatus());
    }

    // ========== Testes de Status Padrão ==========

    @Test
    void statusPadrao_AGUARDANDO() {
        MovieRequestDto dto = new MovieRequestDto();
        assertEquals(StatusFilme.AGUARDANDO, dto.getStatus());
    }

    @Test
    void statusPadraoQuandoAceitaNullESet() {
        MovieRequestDto dto = new MovieRequestDto();
        assertEquals(StatusFilme.AGUARDANDO, dto.getStatus());
        dto.setStatus(null);
        assertNull(dto.getStatus());
    }

    // ========== Testes de Validação - Campos Obrigatórios ==========

    @Test
    void validacao_dtoValido() {
        MovieRequestDto dto = criarDtoValido();
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validacao_tituloNull() {
        MovieRequestDto dto = criarDtoValido();
        dto.setTitulo(null);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("titulo")));
    }

    @Test
    void validacao_tituloVazio() {
        MovieRequestDto dto = criarDtoValido();
        dto.setTitulo("");
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void validacao_tituloEspacosEmBranco() {
        MovieRequestDto dto = criarDtoValido();
        dto.setTitulo("   ");
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void validacao_autorNull() {
        MovieRequestDto dto = criarDtoValido();
        dto.setAutor(null);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("autor")));
    }

    @Test
    void validacao_autorVazio() {
        MovieRequestDto dto = criarDtoValido();
        dto.setAutor("");
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void validacao_sinopseNull() {
        MovieRequestDto dto = criarDtoValido();
        dto.setSinopse(null);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("sinopse")));
    }

    @Test
    void validacao_sinopseVazia() {
        MovieRequestDto dto = criarDtoValido();
        dto.setSinopse("");
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void validacao_anolancamentoNull() {
        MovieRequestDto dto = criarDtoValido();
        dto.setAnoLancamento(null);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("anolancamento")));
    }

    // ========== Testes de Validação - Ano de Lançamento ==========

    @Test
    void validacao_anolancamento1888() {
        MovieRequestDto dto = criarDtoValido();
        dto.setAnoLancamento(1888);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validacao_anolancamento2100() {
        MovieRequestDto dto = criarDtoValido();
        dto.setAnoLancamento(2100);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validacao_anolancamentoMenorQue1888() {
        MovieRequestDto dto = criarDtoValido();
        dto.setAnoLancamento(1887);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("1888")));
    }

    @Test
    void validacao_anolancamentoMaiorQue2100() {
        MovieRequestDto dto = criarDtoValido();
        dto.setAnoLancamento(2101);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("2100")));
    }

    @Test
    void validacao_anolancamenoValido() {
        MovieRequestDto dto = criarDtoValido();
        dto.setAnoLancamento(1950);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    // ========== Testes de Validação - Duração ==========

    @Test
    void validacao_duraçaoPositiva() {
        MovieRequestDto dto = criarDtoValido();
        dto.setDuracao(90);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validacao_duraçaoZero() {
        MovieRequestDto dto = criarDtoValido();
        dto.setDuracao(0);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("positivo")));
    }

    @Test
    void validacao_duraçaoNegativa() {
        MovieRequestDto dto = criarDtoValido();
        dto.setDuracao(-100);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void validacao_duraçaoMuitoGrande() {
        MovieRequestDto dto = criarDtoValido();
        dto.setDuracao(Integer.MAX_VALUE);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validacao_duraçaoUM() {
        MovieRequestDto dto = criarDtoValido();
        dto.setDuracao(1);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    // ========== Testes de Validação - Notas ==========

    @Test
    void validacao_notaDivinaZero() {
        MovieRequestDto dto = criarDtoValido();
        dto.setNotaDivina(0.0);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validacao_notaDivinaDez() {
        MovieRequestDto dto = criarDtoValido();
        dto.setNotaDivina(10.0);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validacao_notaDivinaNegativa() {
        MovieRequestDto dto = criarDtoValido();
        dto.setNotaDivina(-0.1);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("minimo")));
    }

    @Test
    void validacao_notaDivinaMaiorQueDez() {
        MovieRequestDto dto = criarDtoValido();
        dto.setNotaDivina(10.1);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("maximo")));
    }

    @Test
    void validacao_notaPublicoValida() {
        MovieRequestDto dto = criarDtoValido();
        dto.setNotaPublico(5.5);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validacao_notaPublicoNegativa() {
        MovieRequestDto dto = criarDtoValido();
        dto.setNotaPublico(-0.1);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void validacao_notaPublicoMaiorQueDez() {
        MovieRequestDto dto = criarDtoValido();
        dto.setNotaPublico(10.1);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    // ========== Testes de Validação - Campos Opcionais ==========

    @Test
    void validacao_generosNull() {
        MovieRequestDto dto = criarDtoValido();
        dto.setGeneros(null);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validacao_generosVazio() {
        MovieRequestDto dto = criarDtoValido();
        dto.setGeneros(List.of());
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validacao_motivoRecomendacaoNull() {
        MovieRequestDto dto = criarDtoValido();
        dto.setMotivoRecomendacao(null);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validacao_motivoRecomendacaoVazio() {
        MovieRequestDto dto = criarDtoValido();
        dto.setMotivoRecomendacao("");
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validacao_posterNull() {
        MovieRequestDto dto = criarDtoValido();
        dto.setPoster(null);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validacao_plataformasNull() {
        MovieRequestDto dto = criarDtoValido();
        dto.setPlataformas(null);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validacao_plataformasVazio() {
        MovieRequestDto dto = criarDtoValido();
        dto.setPlataformas(List.of());
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validacao_multiplasViolacoes() {
        MovieRequestDto dto = new MovieRequestDto();
        dto.setTitulo(null);
        dto.setAutor("");
        dto.setSinopse("   ");
        dto.setAnoLancamento(1800);
        dto.setDuracao(-50);
        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.size() >= 5);
    }

    // ========== Testes de Comportamento Combinado ==========

    @Test
    void validacao_dtoComTodosOsCamposPreenchidos() {
        MovieRequestDto dto = new MovieRequestDto();
        dto.setTitulo("Filme Completo");
        dto.setAutor("Autor Completo");
        dto.setAnoLancamento(2019);
        dto.setDuracao(180);
        dto.setGeneros(List.of("Action", "Thriller"));
        dto.setSinopse("Sinopse completa");
        dto.setNotaDivina(9.5);
        dto.setNotaPublico(8.5);
        dto.setMotivoRecomendacao("Filme extraordinário");
        dto.setPoster("url_poster");
        Platform netflix = new Platform("Netflix", "url1");
        Platform amazon = new Platform("Amazon", "url2");
        Platform disney = new Platform("Disney+", "url3");
        dto.setPlataformas(List.of(netflix, amazon, disney));
        dto.setStatus(StatusFilme.ATIVO);

        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validacao_dtoMinimo() {
        MovieRequestDto dto = new MovieRequestDto();
        dto.setTitulo("T");
        dto.setAutor("A");
        dto.setAnoLancamento(1999);
        dto.setDuracao(1);
        dto.setSinopse("S");

        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void limitesDeCamposString() {
        MovieRequestDto dto = criarDtoValido();
        dto.setTitulo("A".repeat(1000));
        dto.setAutor("B".repeat(1000));
        dto.setSinopse("C".repeat(5000));

        Set<ConstraintViolation<MovieRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}



