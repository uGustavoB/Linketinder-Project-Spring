package org.ugustavodev.msnucleo.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "Dados cadastrais completos do Candidato (visíveis exclusivamente pelo próprio candidato titular).")
record CandidatoResponseDTO(
        @Schema(description = "ID único do candidato", example = "1")
        Long id,

        @Schema(description = "Primeiro nome do candidato", example = "João")
        String nome,

        @Schema(description = "Sobrenome do candidato", example = "Silva")
        String sobrenome,

        @Schema(description = "Data de nascimento do candidato", example = "1998-05-15")
        LocalDate dataNascimento,

        @Schema(description = "E-mail do candidato", example = "joao.silva@email.com")
        String email,

        @Schema(description = "CPF do candidato (11 dígitos)", example = "12345678901")
        String cpf,

        @Schema(description = "País de residência", example = "Brasil")
        String pais,

        @Schema(description = "Estado ou unidade federativa", example = "SP")
        String estado,

        @Schema(description = "CEP do candidato", example = "01001000")
        String cep,

        @Schema(description = "Resumo profissional do candidato", example = "Desenvolvedor Backend com foco em Java, Groovy e Spring Boot.")
        String descricao,

        @Schema(description = "Lista de formações acadêmicas cadastradas pelo candidato")
        List<FormacaoResponseDTO> formacoes
) {
}
