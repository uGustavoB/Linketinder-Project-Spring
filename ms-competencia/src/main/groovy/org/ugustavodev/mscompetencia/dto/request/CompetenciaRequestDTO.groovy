package org.ugustavodev.mscompetencia.dto.request
import jakarta.validation.constraints.NotBlank

class CompetenciaRequestDTO {
    @NotBlank(message = "O nome da competência é obrigatório")
    String nome
}
