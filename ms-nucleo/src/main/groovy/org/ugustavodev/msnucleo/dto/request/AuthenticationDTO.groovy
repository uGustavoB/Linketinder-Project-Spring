package org.ugustavodev.msnucleo.dto.request

import groovy.transform.Canonical
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@Canonical
class AuthenticationDTO {
    @NotBlank(message = "O campo email é obrigatório")
    @Email(message = "Formato de email inválido")
    String email

    @NotBlank(message = "O campo senha é obrigatório")
    String senha
}
