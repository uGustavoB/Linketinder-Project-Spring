package org.ugustavodev.mscompetencia.usecase.interfaces

import org.ugustavodev.mscompetencia.dto.request.VincularCompetenciaVagaRequestDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVagaVinculoResponseDTO

interface IVincularCompetenciaVagaUseCase {
    List<CompetenciaVagaVinculoResponseDTO> executar(Long vagaId, List<VincularCompetenciaVagaRequestDTO> requestDTOs)
}
