package org.ugustavodev.msnucleo.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Indicadores consolidados e estatísticas de competências do Dashboard para o perfil de Empresa (retornado em GET /api/dashboard)")
record DashboardEmpresaResponseDTO(
        @Schema(description = "Tipo do perfil autenticado", example = "EMPRESA")
        String tipoUsuario,

        @Schema(description = "Total de vagas publicadas pela empresa", example = "4")
        long totalVagasAtivas,

        @Schema(description = "Total de curtidas recebidas nas vagas da empresa", example = "12")
        long totalCurtidasRecebidas,

        @Schema(description = "Total de curtidas enviadas pela empresa aos candidatos", example = "5")
        long totalCurtidasEnviadas,

        @Schema(description = "Total de matches mútuos confirmados nas vagas da empresa", example = "3")
        long totalMatches,

        @Schema(description = "Total de competências vinculadas ao perfil da empresa", example = "5")
        long totalCompetencias,

        @Schema(description = "Distribuição da quantidade de candidatos por competência na plataforma")
        List<CandidatosPorCompetenciaDTO> candidatosPorCompetencia
) {}
