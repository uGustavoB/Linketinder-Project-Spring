package org.ugustavodev.mscompetencia.usecase.interfaces

import org.ugustavodev.mscompetencia.dto.request.VincularCompetenciaRequestDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVinculoResponseDTO

interface IVincularCompetenciaEmpresaUseCase {
    List<CompetenciaVinculoResponseDTO> executar(Long empresaId, List<VincularCompetenciaRequestDTO> requestDTOs)
}
