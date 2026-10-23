package org.ugustavodev.msnucleo.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

import java.time.LocalDateTime

@Schema(description = "Representa o estado de uma curtida entre um Candidato e uma Vaga, indicando se houve Match recíproco.")
class CurtidaResponseDTO {
    @Schema(description = "ID único do registro de curtida", example = "10")
    Long idCurtida

    @Schema(description = "ID do candidato vinculado à curtida", example = "1")
    Long candidatoId

    @Schema(description = "ID da vaga vinculada à curtida", example = "5")
    Long vagaId

    @Schema(description = "Data e hora em que o candidato curtiu a vaga (nulo caso ainda não tenha curtido)", example = "2026-10-23T09:00:00")
    LocalDateTime candidatoCurtiuEm

    @Schema(description = "Data e hora em que a empresa curtiu o candidato para a vaga (nulo caso ainda não tenha curtido)", example = "2026-10-23T09:15:00")
    LocalDateTime empresaCurtiuEm

    @Schema(description = "Indica se ocorreu um Match mútuo (true quando candidatoCurtiuEm e empresaCurtiuEm são ambos não nulos)", example = "true")
    @JsonProperty("match")
    boolean match
}
