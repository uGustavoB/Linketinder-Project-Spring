package org.ugustavodev.msnucleo.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Detalhes de uma Vaga. Os campos empresaId e empresaNome retornam nulos quando consultados em GET /api/vaga/{id} por um candidato que ainda não possui Match na vaga.")
record VagaResponseDTO(
        @Schema(description = "ID único da vaga", example = "5")
        Long id,

        @Schema(description = "Título da vaga", example = "Pessoa Desenvolvedora Backend Groovy/Spring")
        String nome,

        @Schema(description = "Descrição das atividades e requisitos da vaga", example = "Atuação no desenvolvimento e manutenção de microsserviços RESTful com Spring Boot e PostgreSQL.")
        String descricao,

        @Schema(description = "Localidade ou modelo de trabalho", example = "São Paulo - SP (Remoto)")
        String local,

        @Schema(description = "Nível de senioridade da vaga", example = "Pleno")
        String nivel,

        @Schema(description = "ID da empresa autora (nulo para candidatos sem Match na vaga)", example = "1", nullable = true)
        Long empresaId,

        @Schema(description = "Nome da empresa autora (nulo para candidatos sem Match na vaga)", example = "Tech Solutions Ltda", nullable = true)
        String empresaNome
) {
}
