package br.com.blade.indicafilme.dto;

import br.com.blade.indicafilme.model.StatusFilme;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Payload para atualização do status de um filme")
public class StatusPatchDto {

    @NotNull(message = "O campo 'status' é obrigatório.")
    private StatusFilme status;

    public StatusPatchDto() {
    }

    public StatusPatchDto(StatusFilme status) {
        this.status = status;
    }

    public StatusFilme getStatus() {
        return status;
    }

    public void setStatus(StatusFilme status) {
        this.status = status;
    }
}
