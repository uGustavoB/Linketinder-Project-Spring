package org.ugustavodev.msnucleo.dto.response

import java.time.LocalDateTime

record MatchCandidatoResponseDTO(
        VagaPublicoResponseDTO vaga,
        String empresaNome,
        LocalDateTime dataMatch
) {}
