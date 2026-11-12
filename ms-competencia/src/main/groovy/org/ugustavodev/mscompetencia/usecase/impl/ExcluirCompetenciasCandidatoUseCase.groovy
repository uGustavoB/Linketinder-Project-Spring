package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.ugustavodev.mscompetencia.repositories.CompetenciaCandidatoRepository
import org.ugustavodev.mscompetencia.usecase.interfaces.IExcluirCompetenciasCandidatoUseCase

@Service
class ExcluirCompetenciasCandidatoUseCase implements IExcluirCompetenciasCandidatoUseCase {

    private final CompetenciaCandidatoRepository repository

    ExcluirCompetenciasCandidatoUseCase(CompetenciaCandidatoRepository repository) {
        this.repository = repository
    }

    @Override
    @Transactional
    void executar(Long candidatoId, Long candidatoAutenticadoId) {
        if (candidatoId == null) {
            throw new IllegalArgumentException("O ID do candidato não pode ser nulo.")
        }

        if (candidatoAutenticadoId == null || candidatoAutenticadoId != candidatoId) {
            throw new AccessDeniedException("Você não tem permissão para remover as competências deste candidato.")
        }

        repository.deleteByCandidatoId(candidatoId)
    }

}
