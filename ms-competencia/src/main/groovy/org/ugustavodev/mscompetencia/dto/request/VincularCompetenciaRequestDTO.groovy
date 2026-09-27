package org.ugustavodev.mscompetencia.dto.request
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

class VincularCompetenciaRequestDTO {
    @NotNull(message = "O ID da competência é obrigatório")
    Long competenciaId

    @NotBlank(message = "O nível da competência é obrigatório")
    String nivel
}
