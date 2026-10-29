package org.ugustavodev.msnucleo.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Dados cadastrais completos da Empresa (visíveis exclusivamente pela própria empresa titular).")
record EmpresaResponseDTO(
        @Schema(description = "ID único da empresa", example = "1")
        Long id,

        @Schema(description = "Razão social ou nome fantasia da empresa", example = "Tech Solutions Ltda")
        String nome,

        @Schema(description = "CNPJ da empresa (14 dígitos)", example = "12345678000199")
        String cnpj,

        @Schema(description = "E-mail corporativo da empresa", example = "rh@techsolutions.com.br")
        String email,

        @Schema(description = "País sede da empresa", example = "Brasil")
        String pais,

        @Schema(description = "Estado ou unidade federativa da empresa", example = "SP")
        String estado,

        @Schema(description = "CEP da empresa", example = "04538132")
        String cep,

        @Schema(description = "Descrição institucional da empresa", example = "Consultoria especializada em arquitetura de microsserviços e soluções em nuvem.")
        String descricao
) {
}
