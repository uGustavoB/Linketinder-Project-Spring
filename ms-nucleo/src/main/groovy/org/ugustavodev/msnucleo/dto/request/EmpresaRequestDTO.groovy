package org.ugustavodev.msnucleo.dto.request

import groovy.transform.Canonical
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Canonical
@Schema(description = "Dados de entrada para cadastro ou atualização de Empresa. Campos de texto sofrem trim(), " +
        "e-mail é convertido para minúsculas e CNPJ/CEP têm caracteres não numéricos removidos automaticamente.")
class EmpresaRequestDTO {

    @Schema(description = "Razão social ou nome fantasia da empresa", example = "Tech Solutions Ltda", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O campo nome é obrigatório")
    String nome

    @Schema(description = "CNPJ único contendo 14 dígitos numéricos (pontuação é removida automaticamente)", example = "12345678000199", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O campo cnpj é obrigatório")
    @Size(min = 14, max = 14, message = "O CNPJ deve ter 14 dígitos")
    String cnpj

    @Schema(description = "E-mail corporativo único da empresa", example = "rh@techsolutions.com.br", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O campo email é obrigatório")
    @Email(message = "O campo email deve ser um email válido")
    String email

    @Schema(description = "Senha de acesso (mínimo de 6 caracteres; obrigatória no cadastro e opcional na atualização)", example = "senha123")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    String senha

    @Schema(description = "País sede da empresa", example = "Brasil", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "O campo país é obrigatório")
    String pais

    @Schema(description = "Estado ou unidade federativa da empresa", example = "SP")
    String estado

    @Schema(description = "Código postal (CEP, pontuação removida automaticamente)", example = "04538132")
    String cep

    @Schema(description = "Descrição institucional e área de atuação da empresa", example = "Consultoria especializada em arquitetura de microsserviços e soluções em nuvem.")
    String descricao

    void setNome(String nome) {
        this.nome = nome?.trim()
    }

    void setCnpj(String cnpj) {
        this.cnpj = cnpj?.replaceAll("[^0-9]", "")
    }

    void setEmail(String email) {
        this.email = email?.trim()?.toLowerCase()
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
