package org.ugustavodev.msnucleo.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "Dados da Vaga e da Empresa revelados para o Candidato após confirmação de Match (inclui nome e e-mail da empresa e data do match; mantém CNPJ e CEP ocultos).")
record MatchCandidatoResponseDTO(
        @Schema(description = "Dados da vaga na qual ocorreu o Match")
        VagaPublicoResponseDTO vaga,

        @Schema(description = "Nome da empresa contratante (revelado pós-Match)", example = "Tech Solutions Ltda")
        String empresaNome,

        @Schema(description = "E-mail corporativo para contato direto com a empresa (revelado pós-Match)", example = "rh@techsolutions.com.br")
        String empresaEmail,

        @Schema(description = "País da empresa", example = "Brasil")
        String empresaPais,

        @Schema(description = "Estado da empresa", example = "SP")
        String empresaEstado,

        @Schema(description = "Descrição institucional da empresa", example = "Consultoria especializada em arquitetura de microsserviços e soluções em nuvem.")
        String empresaDescricao,

        @Schema(description = "Data e hora em que o Match foi consolidado (timestamp da segunda curtida)", example = "2026-10-23T09:15:00")
        LocalDateTime dataMatch
) {}
