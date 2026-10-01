package org.ugustavodev.mscompetencia.usecase.interfaces
import org.ugustavodev.mscompetencia.dto.request.VincularCompetenciaRequestDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVinculoResponseDTO
interface IVincularCompetenciaCandidatoUseCase { 
    List<CompetenciaVinculoResponseDTO> executar(Long candidatoId, List<VincularCompetenciaRequestDTO> requestDTOs) 
}
