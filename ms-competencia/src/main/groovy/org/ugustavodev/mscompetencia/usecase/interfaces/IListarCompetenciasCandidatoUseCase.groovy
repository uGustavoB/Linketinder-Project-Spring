package org.ugustavodev.mscompetencia.usecase.interfaces
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVinculoResponseDTO
interface IListarCompetenciasCandidatoUseCase { List<CompetenciaVinculoResponseDTO> executar(Long candidatoId) }
