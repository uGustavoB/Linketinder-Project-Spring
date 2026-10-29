package org.ugustavodev.mscompetencia.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import org.ugustavodev.mscompetencia.model.enums.NivelCompetencia

@Schema(description = "Dados do vínculo de uma competência com nível de proficiência associada a um candidato ou empresa")
record CompetenciaVinculoResponseDTO(
        @Schema(description = "ID único do vínculo", example = "10")
        Long idVinculo,

        @Schema(description = "Dados da competência vinculada")
        CompetenciaResponseDTO competencia,

        @Schema(description = "Nível de proficiência na competência", example = "AVANCADO")
        NivelCompetencia nivel
) {
}
