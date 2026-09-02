package org.ugustavodev.msnucleo.dto.response

record VagaResponseDTO(
        Long id,
        String nome,
        String descricao,
        String local,
        String nivel,
        Long empresaId,
        String empresaNome,
        List<Long> competenciaIds
) {
}
