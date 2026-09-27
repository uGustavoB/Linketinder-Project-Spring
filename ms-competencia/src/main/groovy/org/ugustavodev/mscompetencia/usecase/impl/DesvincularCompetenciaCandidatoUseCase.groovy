package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.stereotype.Service
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
    void executar(Long vinculoId) {
        CompetenciaCandidato vinculo = repository.findById(vinculoId)
                .orElseThrow(() -> new NoSuchElementException("Vínculo não encontrado."))
        repository.delete(vinculo)
    }
}
