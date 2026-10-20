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

    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    String senha

    @NotBlank(message = "O campo país é obrigatório")
    String pais

    String estado
    String cep
    String descricao

    void setNome(String nome) {
        this.nome = nome?.trim()
    }

    void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome?.trim()
    }

    void setEmail(String email) {
        this.email = email?.trim()?.toLowerCase()
    }

    void setCpf(String cpf) {
        this.cpf = cpf?.replaceAll("[^0-9]", "")
    }

    void setPais(String pais) {
        this.pais = pais?.trim()
    }

    void setEstado(String estado) {
        this.estado = estado?.trim()
    }

    void setCep(String cep) {
        this.cep = cep?.replaceAll("[^0-9]", "")
    }

    void setDescricao(String descricao) {
        this.descricao = descricao?.trim()
    }

    void setSenha(String senha) {
        this.senha = senha?.trim() ? senha.trim() : null
    }

}
