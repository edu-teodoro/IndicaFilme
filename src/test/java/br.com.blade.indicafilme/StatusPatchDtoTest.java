package br.com.blade.indicafilme;

import br.com.blade.indicafilme.dto.StatusPatchDto;
import br.com.blade.indicafilme.model.StatusFilme;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class StatusPatchDtoTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ========== Testes de Getters e Setters ==========

    @Test
    void construtor_padrao() {
        StatusPatchDto dto = new StatusPatchDto();
        assertNull(dto.getStatus());
    }

    @Test
    void construtor_comParametro() {
        StatusPatchDto dto = new StatusPatchDto(StatusFilme.ATIVO);
        assertEquals(StatusFilme.ATIVO, dto.getStatus());
    }

    @Test
    void setter_atualizaStatus() {
        StatusPatchDto dto = new StatusPatchDto();
        dto.setStatus(StatusFilme.ATIVO);
        assertEquals(StatusFilme.ATIVO, dto.getStatus());
    }

    @Test
    void setter_atualizaParaAGUARDANDO() {
        StatusPatchDto dto = new StatusPatchDto();
        dto.setStatus(StatusFilme.AGUARDANDO);
        assertEquals(StatusFilme.AGUARDANDO, dto.getStatus());
    }

    @Test
    void setter_atualizaParaERRO() {
        StatusPatchDto dto = new StatusPatchDto();
        dto.setStatus(StatusFilme.ERRO);
        assertEquals(StatusFilme.ERRO, dto.getStatus());
    }

    // ========== Testes de Validação ==========

    @Test
    void validacao_statusAtivo() {
        StatusPatchDto dto = new StatusPatchDto(StatusFilme.ATIVO);
        Set<ConstraintViolation<StatusPatchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validacao_statusAgUARDANDO() {
        StatusPatchDto dto = new StatusPatchDto(StatusFilme.AGUARDANDO);
        Set<ConstraintViolation<StatusPatchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validacao_statusErro() {
        StatusPatchDto dto = new StatusPatchDto(StatusFilme.ERRO);
        Set<ConstraintViolation<StatusPatchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validacao_statusNull() {
        StatusPatchDto dto = new StatusPatchDto();
        Set<ConstraintViolation<StatusPatchDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("obrigatório")));
    }

    @Test
    void validacao_setarNullAposConstructor() {
        StatusPatchDto dto = new StatusPatchDto(StatusFilme.ATIVO);
        dto.setStatus(null);
        Set<ConstraintViolation<StatusPatchDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    // ========== Testes de Enum StatusFilme ==========

    @Test
    void validacao_todosOsStatusValidos() {
        for (StatusFilme status : StatusFilme.values()) {
            StatusPatchDto dto = new StatusPatchDto(status);
            Set<ConstraintViolation<StatusPatchDto>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty(), "Status " + status + " deveria ser válido");
        }
    }

    @Test
    void transicaoDeStatus_ativoParaAguardando() {
        StatusPatchDto dto = new StatusPatchDto(StatusFilme.ATIVO);
        dto.setStatus(StatusFilme.AGUARDANDO);
        assertEquals(StatusFilme.AGUARDANDO, dto.getStatus());
        Set<ConstraintViolation<StatusPatchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void transicaoDeStatus_aguardandoParaAtivo() {
        StatusPatchDto dto = new StatusPatchDto(StatusFilme.AGUARDANDO);
        dto.setStatus(StatusFilme.ATIVO);
        assertEquals(StatusFilme.ATIVO, dto.getStatus());
        Set<ConstraintViolation<StatusPatchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void transicaoDeStatus_erroParaAtivo() {
        StatusPatchDto dto = new StatusPatchDto(StatusFilme.ERRO);
        dto.setStatus(StatusFilme.ATIVO);
        assertEquals(StatusFilme.ATIVO, dto.getStatus());
        Set<ConstraintViolation<StatusPatchDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    // ========== Testes de Edge Cases ==========

    @Test
    void multiplosConstrutoresComValoresIguais() {
        StatusPatchDto dto1 = new StatusPatchDto(StatusFilme.ATIVO);
        StatusPatchDto dto2 = new StatusPatchDto(StatusFilme.ATIVO);
        assertEquals(dto1.getStatus(), dto2.getStatus());
    }

    @Test
    void setarMuliplasVezesOMesmoStatus() {
        StatusPatchDto dto = new StatusPatchDto();
        dto.setStatus(StatusFilme.ATIVO);
        dto.setStatus(StatusFilme.ATIVO);
        dto.setStatus(StatusFilme.ATIVO);
        assertEquals(StatusFilme.ATIVO, dto.getStatus());
    }
}
