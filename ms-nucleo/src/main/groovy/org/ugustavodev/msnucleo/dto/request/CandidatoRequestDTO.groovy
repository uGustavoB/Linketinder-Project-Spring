package org.ugustavodev.msnucleo.dto.request

import groovy.transform.Canonical
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

import java.time.LocalDate

@Canonical
@Schema(description = "Dados de entrada para cadastro ou atualização de Candidato. Campos de texto sofrem trim(), e-mail é convertido para minúsculas e CPF/CEP têm caracteres não numéricos removidos automaticamente.")
class CandidatoRequestDTO {
    @Schema(description = "Primeiro nome do candidato", example = "João", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O campo nome é obrigatório")
    String nome

    @Schema(description = "Sobrenome do candidato", example = "Silva", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O campo sobrenome é obrigatório")
    String sobrenome

    @Schema(description = "Data de nascimento no formato ISO (AAAA-MM-DD)", example = "1998-05-15", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "O campo data de nascimento é obrigatório")
    LocalDate dataNascimento

    @Schema(description = "E-mail único do candidato", example = "joao.silva@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O campo email é obrigatório")
    @Email(message = "O campo email deve ser um email válido")
    String email

    @Schema(description = "CPF único contendo 11 dígitos numéricos (pontuação é removida automaticamente)", example = "12345678901", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O campo cpf é obrigatório")
    @Size(min = 11, max = 11, message = "O CPF deve ter 11 dígitos")
    String cpf

    @Schema(description = "Senha de acesso (mínimo de 6 caracteres; obrigatória no cadastro e opcional na atualização)", example = "senha123")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    String senha

    @Schema(description = "País de residência", example = "Brasil", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O campo país é obrigatório")
    String pais

    @Schema(description = "Estado ou unidade federativa", example = "SP")
    String estado

    @Schema(description = "Código postal (CEP, pontuação removida automaticamente)", example = "01001000")
    String cep

    @Schema(description = "Resumo profissional e apresentação do candidato", example = "Desenvolvedor Backend com foco em Java, Groovy e Spring Boot.")
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
