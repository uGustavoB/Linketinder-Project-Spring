package org.ugustavodev.mscompetencia.dto.response

import org.ugustavodev.mscompetencia.model.enums.NivelCompetencia

record CompetenciaVinculoResponseDTO(Long idVinculo, CompetenciaResponseDTO competencia, NivelCompetencia nivel) {}
