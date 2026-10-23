package org.ugustavodev.msnucleo.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Visão pública e anônima do Candidato exibida antes do Match (Princípio do Anonimato). Oculta nome, sobrenome, e-mail, CPF, data de nascimento e CEP.")
record CandidatoPublicoResponseDTO(
    @Schema(description = "ID único do candidato", example = "1")
    Long id,

    @Schema(description = "País de residência", example = "Brasil")
    String pais,

    @Schema(description = "Estado ou unidade federativa", example = "SP")
    String estado,

    @Schema(description = "Descrição e resumo profissional do candidato", example = "Desenvolvedor Backend com experiência em Groovy e Spring Boot.")
    String descricao,

    @Schema(description = "Lista de formações acadêmicas do candidato")
    List<FormacaoResponseDTO> formacoes
) {}
