package org.ugustavodev.mscompetencia.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "Dados de entrada para cadastro de uma nova competência no catálogo global")
class CompetenciaRequestDTO {

    @Schema(description = "Nome da competência técnica ou comportamental", example = "Java", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O nome da competência é obrigatório")
    String nome

}
