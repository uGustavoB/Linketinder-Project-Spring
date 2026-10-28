package org.ugustavodev.msnucleo.dto.request

import groovy.transform.Canonical
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@Canonical
@Schema(description = "Credenciais de entrada para autenticação de Candidato ou Empresa")
class AuthenticationDTO {

    @Schema(description = "E-mail cadastrado do usuário (convertido automaticamente para minúsculas)", example = "candidato@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O campo email é obrigatório")
    @Email(message = "Formato de email inválido")
    String email

    @Schema(description = "Senha do usuário (mínimo de 6 caracteres)", example = "senha123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O campo senha é obrigatório")
    String senha

    void setEmail(String email) {
        this.email = email?.trim()?.toLowerCase()
    }

}
