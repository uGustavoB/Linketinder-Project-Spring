package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.ugustavodev.mscompetencia.model.CompetenciaCandidato
import org.ugustavodev.mscompetencia.repositories.CompetenciaCandidatoRepository
import org.ugustavodev.mscompetencia.usecase.interfaces.IDesvincularCompetenciaCandidatoUseCase

@Service
class DesvincularCompetenciaCandidatoUseCase implements IDesvincularCompetenciaCandidatoUseCase {

    private final CompetenciaCandidatoRepository repository

    DesvincularCompetenciaCandidatoUseCase(CompetenciaCandidatoRepository repository) {
        this.repository = repository
    }

    @Override
    @Transactional
    void executar(Long vinculoId, Long candidatoAutenticadoId) {
        if (vinculoId == null) {
            throw new IllegalArgumentException("O ID do vínculo não pode ser nulo.")
        }

        CompetenciaCandidato vinculo = repository.findById(vinculoId)
                .orElseThrow { new NoSuchElementException("Vínculo não encontrado.") }

        if (candidatoAutenticadoId == null || vinculo.candidatoId != candidatoAutenticadoId) {
            throw new AccessDeniedException("Você não tem permissão para remover este vínculo.")
        }

        repository.delete(vinculo)
    }

}
