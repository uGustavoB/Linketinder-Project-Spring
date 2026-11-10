package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.ugustavodev.mscompetencia.dto.response.CandidatosPorCompetenciaResponseDTO
import org.ugustavodev.mscompetencia.repositories.CompetenciaCandidatoRepository
import org.ugustavodev.mscompetencia.usecase.interfaces.IContarCandidatosPorCompetenciaUseCase

@Service
class ContarCandidatosPorCompetenciaUseCase implements IContarCandidatosPorCompetenciaUseCase {
    private final CompetenciaCandidatoRepository repository

    ContarCandidatosPorCompetenciaUseCase(CompetenciaCandidatoRepository repository) {
        this.repository = repository
    }

    @Override
    @Transactional(readOnly = true)
    List<CandidatosPorCompetenciaResponseDTO> executar() {
        return repository.contarCandidatosPorCompetencia()
    }
}
