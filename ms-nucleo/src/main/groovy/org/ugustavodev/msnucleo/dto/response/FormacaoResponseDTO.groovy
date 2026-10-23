package org.ugustavodev.msnucleo.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "Dados de uma Formação Acadêmica vinculada ao perfil de um candidato")
record FormacaoResponseDTO(
        @Schema(description = "ID único da formação acadêmica", example = "1")
        Long id,

        @Schema(description = "Instituição de ensino", example = "Universidade de São Paulo (USP)")
        String instituicao,

        @Schema(description = "Nome do curso", example = "Ciência da Computação")
        String curso,

        @Schema(description = "Nível acadêmico", example = "Bacharelado")
        String nivel,

        @Schema(description = "Status da formação", example = "Concluído")
        String status,

        @Schema(description = "Data de início", example = "2020-02-01")
        LocalDate dataInicio,

        @Schema(description = "Data de conclusão", example = "2023-12-15")
        LocalDate dataConclusao
) {}
