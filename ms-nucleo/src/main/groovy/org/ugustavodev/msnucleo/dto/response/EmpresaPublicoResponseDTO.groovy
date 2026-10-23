package org.ugustavodev.msnucleo.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Visão pública e anônima da Empresa exibida antes do Match (Princípio do Anonimato). Oculta nome, CNPJ, e-mail e CEP.")
record EmpresaPublicoResponseDTO(
    @Schema(description = "ID único da empresa", example = "1")
    Long id,

    @Schema(description = "País sede da empresa", example = "Brasil")
    String pais,

    @Schema(description = "Estado ou unidade federativa da empresa", example = "SP")
    String estado,

    @Schema(description = "Descrição institucional da empresa", example = "Consultoria especializada em arquitetura de microsserviços e soluções em nuvem.")
    String descricao
) {}
