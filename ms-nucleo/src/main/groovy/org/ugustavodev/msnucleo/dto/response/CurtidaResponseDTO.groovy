package org.ugustavodev.msnucleo.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

import java.time.LocalDateTime

class CurtidaResponseDTO {
    Long idCurtida
    Long candidatoId
    Long vagaId
    LocalDateTime candidatoCurtiuEm
    LocalDateTime empresaCurtiuEm

    @JsonProperty("match")
    boolean match
}
