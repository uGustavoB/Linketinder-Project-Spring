package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.ugustavodev.mscompetencia.model.CompetenciaVaga
import org.ugustavodev.mscompetencia.repositories.CompetenciaVagaRepository
import org.ugustavodev.mscompetencia.usecase.interfaces.IDesvincularCompetenciaVagaUseCase

@Service
class DesvincularCompetenciaVagaUseCase implements IDesvincularCompetenciaVagaUseCase {
    private final CompetenciaVagaRepository repository

    DesvincularCompetenciaVagaUseCase(CompetenciaVagaRepository repository) {
        this.repository = repository
    }

    @Override
    @Transactional
    void executar(Long vinculoId) {
        if (vinculoId == null) {
            throw new IllegalArgumentException("O ID do vínculo não pode ser nulo.")
        }

        CompetenciaVaga vinculo = repository.findById(vinculoId)
                .orElseThrow(() -> new NoSuchElementException("Vínculo não encontrado."))
        repository.delete(vinculo)
    }
}
