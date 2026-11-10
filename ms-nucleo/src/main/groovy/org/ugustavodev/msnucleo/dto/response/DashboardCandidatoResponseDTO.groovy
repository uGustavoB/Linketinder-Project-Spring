package org.ugustavodev.msnucleo.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Indicadores consolidados do Dashboard para o perfil de Candidato (retornado em GET /api/dashboard)")
record DashboardCandidatoResponseDTO(
        @Schema(description = "Tipo do perfil autenticado", example = "CANDIDATO")
        String tipoUsuario,

        @Schema(description = "Total de vagas disponíveis ainda não curtidas pelo candidato", example = "14")
        long totalVagasSugeridas,

        @Schema(description = "Total de curtidas enviadas pelo candidato em vagas", example = "8")
        long totalCurtidasEnviadas,

        @Schema(description = "Total de curtidas recebidas de empresas", example = "5")
        long totalCurtidasRecebidas,

        @Schema(description = "Total de matches mútuos confirmados", example = "2")
        long totalMatches,

        @Schema(description = "Total de competências técnicas vinculadas ao perfil do candidato", example = "6")
        long totalCompetencias
) {}
