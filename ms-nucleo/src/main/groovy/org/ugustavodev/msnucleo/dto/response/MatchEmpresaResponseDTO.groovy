package org.ugustavodev.msnucleo.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "Dados da Vaga e do Candidato revelados para a Empresa após confirmação de Match (inclui " +
        "detalhes da vaga, dados públicos do candidato, nome completo, e-mail e data do match; mantém CPF, CEP e data de nascimento ocultos).")
record MatchEmpresaResponseDTO(
        @Schema(description = "Dados da vaga na qual ocorreu o Match")
        VagaPublicoResponseDTO vaga,

        @Schema(description = "Dados públicos e formações acadêmicas do candidato")
        CandidatoPublicoResponseDTO candidato,

        @Schema(description = "Nome completo do candidato (nome + sobrenome, revelado pós-Match)", example = "João Silva")
        String candidatoNome,

        @Schema(description = "E-mail do candidato para contato direto (revelado pós-Match)", example = "joao.silva@email.com")
        String candidatoEmail,

        @Schema(description = "Data e hora em que o Match foi consolidado (timestamp da segunda curtida)", example = "2026-10-23T09:15:00")
        LocalDateTime dataMatch
) {}
