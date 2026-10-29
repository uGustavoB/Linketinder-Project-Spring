package org.ugustavodev.mscompetencia.model.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Nível de proficiência ou exigência de uma competência (BASICO, MEDIO, AVANCADO)", example = "AVANCADO")
enum NivelCompetencia {
    BASICO,
    MEDIO,
    AVANCADO
}
