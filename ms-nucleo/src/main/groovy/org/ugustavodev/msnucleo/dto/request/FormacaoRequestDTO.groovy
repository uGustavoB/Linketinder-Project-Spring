package org.ugustavodev.msnucleo.dto.request

import groovy.transform.Canonical
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

@Canonical
@Schema(description = "Dados de entrada para cadastro ou atualização de Formação Acadêmica do candidato autenticado")
class FormacaoRequestDTO {
    @Schema(description = "Nome da instituição de ensino", example = "Universidade de São Paulo (USP)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Instituição é obrigatória")
    String instituicao

    @Schema(description = "Nome do curso acadêmico", example = "Ciência da Computação", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Curso é obrigatório")
    String curso

    @Schema(description = "Nível da formação acadêmica", example = "Bacharelado", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Nível é obrigatório")
    String nivel

    @Schema(description = "Situação atual do curso (Concluído, Em andamento, Trancado)", example = "Concluído", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Status é obrigatório")
    String status

    @Schema(description = "Data de início da formação (AAAA-MM-DD)", example = "2020-02-01")
    LocalDate dataInicio

    @Schema(description = "Data de conclusão ou previsão de término (AAAA-MM-DD)", example = "2023-12-15")
    LocalDate dataConclusao
}
