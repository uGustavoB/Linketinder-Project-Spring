package org.ugustavodev.mscompetencia.usecase.interfaces

import org.ugustavodev.mscompetencia.dto.response.CompetenciaVinculoResponseDTO

interface IListarCompetenciasVagaUseCase {
    List<CompetenciaVinculoResponseDTO> executar(Long vagaId)
}
