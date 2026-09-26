package org.ugustavodev.msnucleo.dto.response

import java.time.LocalDateTime

class CurtidaResponseDTO {
    Long id
    Long candidatoId
    Long vagaId
    LocalDateTime candidatoCurtiuEm
    LocalDateTime empresaCurtiuEm
    boolean isMatch
}
