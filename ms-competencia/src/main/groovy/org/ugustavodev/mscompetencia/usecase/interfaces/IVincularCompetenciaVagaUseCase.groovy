package org.ugustavodev.mscompetencia.usecase.interfaces

import org.ugustavodev.mscompetencia.dto.request.VincularCompetenciaRequestDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVinculoResponseDTO

interface IVincularCompetenciaVagaUseCase {
    CompetenciaVinculoResponseDTO executar(Long vagaId, VincularCompetenciaRequestDTO requestDTO)
}
