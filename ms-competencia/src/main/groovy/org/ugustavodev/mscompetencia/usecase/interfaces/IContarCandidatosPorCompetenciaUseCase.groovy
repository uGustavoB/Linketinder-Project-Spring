package org.ugustavodev.mscompetencia.usecase.interfaces

import org.ugustavodev.mscompetencia.dto.response.CandidatosPorCompetenciaResponseDTO

interface IContarCandidatosPorCompetenciaUseCase {
    List<CandidatosPorCompetenciaResponseDTO> executar()
}
