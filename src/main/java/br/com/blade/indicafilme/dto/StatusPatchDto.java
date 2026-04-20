package br.com.blade.indicafilme.dto;

import br.com.blade.indicafilme.model.StatusFilme;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;


/**
 * DTO utilizado no endpoint {@code PATCH /api/v1/admin/movies/{id}/status}.
 *
 * Permite atualizar apenas o status de curadoria de um filme, sem
 * alterar nenhum outro campo. Um filme com status {@code ATIVO} aparece
 * para os usuários; qualquer outro status o oculta.
 */
@Schema(description = "Payload para atualização do status de um filme")
public class StatusPatchDto {

    @NotNull(message = "O campo 'status' é obrigatório.")
    @Schema(
            description = "Novo status do filme. ATIVO = visível para usuários; AGUARDANDO/ERRO = oculto.",
            example = "ATIVO",
            allowableValues = {"ATIVO", "AGUARDANDO", "ERRO"}
    )
    private StatusFilme status;

    public StatusPatchDto(){}

    public StatusPatchDto(StatusFilme status) {
        this.status = status;
    }

    public StatusFilme getStatus(){
        return status;
    }

    public void setStatus(StatusFilme status) {
        this.status = status;
    }
}
