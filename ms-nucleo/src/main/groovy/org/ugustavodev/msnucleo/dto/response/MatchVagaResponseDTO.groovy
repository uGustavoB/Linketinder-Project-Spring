package org.ugustavodev.msnucleo.dto.response

import java.time.LocalDateTime

record MatchVagaResponseDTO(
        CandidatoPublicoResponseDTO candidato,
        String candidatoNome,
        LocalDateTime dataMatch
) {}
