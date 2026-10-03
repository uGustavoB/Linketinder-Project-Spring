package org.ugustavodev.mscompetencia.dto.request

import jakarta.validation.constraints.NotNull

class VincularCompetenciaVagaRequestDTO {
    @NotNull(message = "O ID da competência é obrigatório")
    Long competenciaId
}
