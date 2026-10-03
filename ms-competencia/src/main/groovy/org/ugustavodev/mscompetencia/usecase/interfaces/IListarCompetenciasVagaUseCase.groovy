package org.ugustavodev.mscompetencia.usecase.interfaces

import org.ugustavodev.mscompetencia.dto.response.CompetenciaVagaVinculoResponseDTO

interface IListarCompetenciasVagaUseCase {
    List<CompetenciaVagaVinculoResponseDTO> executar(Long vagaId)
}
