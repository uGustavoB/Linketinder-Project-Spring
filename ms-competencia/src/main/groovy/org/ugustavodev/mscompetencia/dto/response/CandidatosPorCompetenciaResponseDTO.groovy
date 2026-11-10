package org.ugustavodev.mscompetencia.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Quantidade de candidatos associados a uma competência do catálogo")
record CandidatosPorCompetenciaResponseDTO(
        @Schema(description = "ID único da competência", example = "1")
        Long competenciaId,

        @Schema(description = "Nome da competência", example = "Java")
        String nomeCompetencia,

        @Schema(description = "Total de candidatos que possuem esta competência", example = "12")
        Long totalCandidatos
) {
}
