package org.ugustavodev.msnucleo.dto.request

import groovy.transform.Canonical
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Canonical
class EmpresaRequestDTO {
    @NotBlank(message = "O campo nome é obrigatório")
    String nome

    @NotBlank(message = "O campo cnpj é obrigatório")
    @Size(min = 14, max = 14, message = "O CNPJ deve ter 14 dígitos")
    String cnpj

    @NotBlank(message = "O campo email é obrigatório")
    @Email(message = "O campo email deve ser um email válido")
    String email

    @NotBlank(message = "O campo da senha é obrigatório")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    String senha

    @NotBlank(message = "O campo país é obrigatório")
    String pais

    String estado
    String cep
    String descricao
}
