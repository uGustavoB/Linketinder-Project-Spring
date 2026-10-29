package org.ugustavodev.msnucleo.dto.request

import groovy.transform.Canonical
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Canonical
@Schema(description = "Dados de entrada para publicação ou atualização de Vaga por uma Empresa autenticada")
class VagaRequestDTO {

    @Schema(description = "Título ou cargo da vaga", example = "Pessoa Desenvolvedora Backend Groovy/Spring", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O campo nome é obrigatório")
    String nome

    @Schema(
            description = "Descrição detalhada das atribuições e requisitos da vaga",
            example = "Atuação no desenvolvimento e manutenção de microsserviços RESTful com Spring Boot e PostgreSQL.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "O campo descrição é obrigatório")
    String descricao

    @Schema(description = "Localidade ou modalidade de trabalho da vaga", example = "São Paulo - SP (Remoto)")
    String local

    @Schema(description = "Senioridade ou nível exigido pela vaga", example = "Pleno")
    String nivel

    void setNome(String nome) {
        this.nome = nome?.trim()
    }

    void setDescricao(String descricao) {
        this.descricao = descricao?.trim()
    }

    void setLocal(String local) {
        this.local = local?.trim()
    }

    void setNivel(String nivel) {
        this.nivel = nivel?.trim()
    }

}
