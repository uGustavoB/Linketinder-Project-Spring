package org.ugustavodev.mscompetencia.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import org.ugustavodev.mscompetencia.model.enums.NivelCompetencia

@Schema(description = "Item de vinculação de uma competência com nível de proficiência para candidato ou empresa")
class VincularCompetenciaRequestDTO {

    @Schema(description = "ID da competência cadastrada no catálogo global", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "O ID da competência é obrigatório")
    Long competenciaId

    @Schema(description = "Nível de proficiência ou domínio na competência", example = "AVANCADO", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "O nível da competência é obrigatório")
    NivelCompetencia nivel

}
