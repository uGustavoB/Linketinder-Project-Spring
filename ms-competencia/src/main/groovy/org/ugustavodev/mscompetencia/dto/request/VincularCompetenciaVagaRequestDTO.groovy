package org.ugustavodev.mscompetencia.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

@Schema(description = "Item de vinculação de uma competência exigida por uma vaga de emprego")
class VincularCompetenciaVagaRequestDTO {

    @Schema(description = "ID da competência cadastrada no catálogo global", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "O ID da competência é obrigatório")
    Long competenciaId

}
