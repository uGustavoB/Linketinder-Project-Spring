package org.ugustavodev.mscompetencia.usecase.interfaces

import org.ugustavodev.mscompetencia.dto.request.VincularCompetenciaRequestDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVinculoResponseDTO

interface IVincularCompetenciaVagaUseCase {
    List<CompetenciaVinculoResponseDTO> executar(Long vagaId, List<VincularCompetenciaRequestDTO> requestDTOs)
}
