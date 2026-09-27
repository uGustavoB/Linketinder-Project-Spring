package org.ugustavodev.mscompetencia.dto.request
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.ugustavodev.mscompetencia.model.enums.NivelCompetencia

class VincularCompetenciaRequestDTO {
    @NotNull(message = "O ID da competência é obrigatório")
    Long competenciaId

    @NotNull(message = "O nível da competência é obrigatório")
    NivelCompetencia nivel
}
