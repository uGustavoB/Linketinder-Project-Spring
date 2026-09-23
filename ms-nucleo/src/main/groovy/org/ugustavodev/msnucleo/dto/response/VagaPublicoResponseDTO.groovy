package org.ugustavodev.msnucleo.dto.response

record VagaPublicoResponseDTO(
    Long id,
    String nome,
    String descricao,
    String local,
    String nivel,
    Long empresaId,
    List<Long> competenciaIds
) {}
