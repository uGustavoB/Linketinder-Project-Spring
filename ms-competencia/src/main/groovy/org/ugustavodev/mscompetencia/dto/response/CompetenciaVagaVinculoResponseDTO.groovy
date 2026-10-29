package org.ugustavodev.mscompetencia.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Dados do vínculo de uma competência exigida em uma vaga de emprego")
record CompetenciaVagaVinculoResponseDTO(
        @Schema(description = "ID único do vínculo com a vaga", example = "25")
        Long idVinculo,

        @Schema(description = "Dados da competência exigida pela vaga")
        CompetenciaResponseDTO competencia
) {
}
