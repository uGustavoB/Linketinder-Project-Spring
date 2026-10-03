package org.ugustavodev.msnucleo.dto.response

record CandidatoPublicoResponseDTO(
    Long id,
    String pais,
    String estado,
    String descricao,
    List<FormacaoResponseDTO> formacoes
) {}
