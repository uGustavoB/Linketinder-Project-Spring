package org.ugustavodev.mscompetencia.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Dados de uma competência pertencente ao catálogo global")
record CompetenciaResponseDTO(
        @Schema(description = "ID único da competência", example = "1")
        Long id,

        @Schema(description = "Nome da competência", example = "Java")
        String nome
) {
}
