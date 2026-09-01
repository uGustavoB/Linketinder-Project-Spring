package org.ugustavodev.msnucleo.dto.request

import groovy.transform.Canonical
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

import java.time.LocalDate

@Canonical
class CandidatoRequestDTO {
    @NotBlank(message = "O campo nome é obrigatório")
    String nome

    @NotBlank(message = "O campo sobrenome é obrigatório")
    String sobrenome

    @NotNull(message = "O campo data de nascimento é obrigatório")
    LocalDate dataNascimento

    @NotBlank(message = "O campo email é obrigatório")
    @Email(message = "O campo email deve ser um email válido")
    String email

    @NotBlank(message = "O campo cpf é obrigatório")
    @Size(min = 11, max = 11, message = "O CPF deve ter 11 dígitos")
    String cpf

    @NotBlank(message = "O campo da senha é obrigatório")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    String senha

    @NotBlank(message = "O campo país é obrigatório")
    String pais

    String estado
    String cep
    String descricao
}
