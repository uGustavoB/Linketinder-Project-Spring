package org.ugustavodev.msnucleo.dto.response

import java.time.LocalDateTime

record MatchCandidatoResponseDTO(
        VagaPublicoResponseDTO vaga,
        String empresaNome,
        String empresaEmail,
        String empresaPais,
        String empresaEstado,
        String empresaDescricao,
        LocalDateTime dataMatch
) {}
