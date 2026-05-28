package br.com.blade.indicafilme;

import br.com.blade.indicafilme.dto.DivinePatchDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DivinePatchDtoTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ========== Testes de Getters e Setters ==========

    @Test
    void construtor_padrao() {
        DivinePatchDto dto = new DivinePatchDto();
        assertNull(dto.getNotaDivina());
        assertNull(dto.getMotivoRecomendacao());
    }

    @Test
    void setter_notaDivina() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setNotaDivina(7.5);
        assertEquals(7.5, dto.getNotaDivina());
    }

    @Test
    void setter_motivoRecomendacao() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setMotivoRecomendacao("Excelente filme");
        assertEquals("Excelente filme", dto.getMotivoRecomendacao());
    }

    @Test
    void setter_ambos() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setNotaDivina(8.0);
        dto.setMotivoRecomendacao("Recomendo");
        assertEquals(8.0, dto.getNotaDivina());
        assertEquals("Recomendo", dto.getMotivoRecomendacao());
    }

    // ========== Testes de Validação de NotaDivina ==========

    @Test
    void validacao_notaDivinaZero() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setNotaDivina(0.0);
        Set<ConstraintViolation<DivinePatchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validacao_notaDivinaDez() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setNotaDivina(10.0);
        Set<ConstraintViolation<DivinePatchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validacao_notaDivinaValida() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setNotaDivina(5.5);
        Set<ConstraintViolation<DivinePatchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validacao_notaDivinaNegativa() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setNotaDivina(-0.1);
        Set<ConstraintViolation<DivinePatchDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("minimo")));
    }

    @Test
    void validacao_notaDivinaMaiorQueD() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setNotaDivina(10.1);
        Set<ConstraintViolation<DivinePatchDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("maximo")));
    }

    @Test
    void validacao_notaDivinaMuitoGrande() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setNotaDivina(100.0);
        Set<ConstraintViolation<DivinePatchDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    // ========== Testes de Validação de MotivoRecomendacao ==========

    @Test
    void validacao_motivoRecomendacaoVacio() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setMotivoRecomendacao("");
        Set<ConstraintViolation<DivinePatchDto>> violations = validator.validate(dto);
        // Sem validação @NotBlank, string vazia deve ser válida
        assertTrue(violations.isEmpty());
    }

    @Test
    void validacao_motivoRecomendacoaoNull() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setMotivoRecomendacao(null);
        Set<ConstraintViolation<DivinePatchDto>> violations = validator.validate(dto);
        // Sem validação @NotNull, null deve ser válido
        assertTrue(violations.isEmpty());
    }

    @Test
    void validacao_motivoRecomendacaoComTexto() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setMotivoRecomendacao("Filme com ótima cinematografia");
        Set<ConstraintViolation<DivinePatchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validacao_motivoRecomendacaoComEspacos() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setMotivoRecomendacao("   Espaços em branco   ");
        Set<ConstraintViolation<DivinePatchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    // ========== Testes de Comportamento de campos Opcionais ==========

    @Test
    void ambosOpcionales_notaDivinaNullMotivoPreenchido() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setNotaDivina(null);
        dto.setMotivoRecomendacao("Motivo");
        assertNull(dto.getNotaDivina());
        assertEquals("Motivo", dto.getMotivoRecomendacao());
        Set<ConstraintViolation<DivinePatchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void ambosOpcionales_notaDivinaPreenchidaMotivoNull() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setNotaDivina(7.0);
        dto.setMotivoRecomendacao(null);
        assertEquals(7.0, dto.getNotaDivina());
        assertNull(dto.getMotivoRecomendacao());
        Set<ConstraintViolation<DivinePatchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void ambosOpcionales_ambosnull() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setNotaDivina(null);
        dto.setMotivoRecomendacao(null);
        assertNull(dto.getNotaDivina());
        assertNull(dto.getMotivoRecomendacao());
        Set<ConstraintViolation<DivinePatchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void ambosOpcionales_ambosPreenchidos() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setNotaDivina(9.5);
        dto.setMotivoRecomendacao("Filme excelente");
        assertEquals(9.5, dto.getNotaDivina());
        assertEquals("Filme excelente", dto.getMotivoRecomendacao());
        Set<ConstraintViolation<DivinePatchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    // ========== Testes de Edge Cases ==========

    @Test
    void notaDivina_limiteInferioPreciso() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setNotaDivina(0.0);
        Set<ConstraintViolation<DivinePatchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void notaDivina_limiteSuperiorPreciso() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setNotaDivina(10.0);
        Set<ConstraintViolation<DivinePatchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void notaDivina_apenasAcimaDeLimiteInferior() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setNotaDivina(0.001);
        Set<ConstraintViolation<DivinePatchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void notaDivina_apenasAbaixoDeLimiteSuperior() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setNotaDivina(9.999);
        Set<ConstraintViolation<DivinePatchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void motivoRecomendacao_textoMuitoLongo() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setMotivoRecomendacao("A".repeat(1000));
        Set<ConstraintViolation<DivinePatchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void motivoRecomendacao_comCaracteresEspeciais() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setMotivoRecomendacao("Filme com caracteres: @#$%&*()?!~");
        Set<ConstraintViolation<DivinePatchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void motivoRecomendacao_comUnicodeEAcentos() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setMotivoRecomendacao("Filme incrível com personagens inesperados");
        Set<ConstraintViolation<DivinePatchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void multiplosSettersAlteramValores() {
        DivinePatchDto dto = new DivinePatchDto();
        dto.setNotaDivina(5.0);
        dto.setNotaDivina(7.5);
        dto.setNotaDivina(9.0);
        assertEquals(9.0, dto.getNotaDivina());
        Set<ConstraintViolation<DivinePatchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void notaDivina_withDecimalsVariacao() {
        DivinePatchDto dto = new DivinePatchDto();
        double[] notas = {1.1, 2.2, 3.3, 4.4, 5.5, 6.6, 7.7, 8.8, 9.9};
        for (double nota : notas) {
            dto.setNotaDivina(nota);
            Set<ConstraintViolation<DivinePatchDto>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty(), "Nota " + nota + " deveria ser válida");
        }
    }
}

