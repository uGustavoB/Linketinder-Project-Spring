package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.security.access.AccessDeniedException
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
    void executar(Long vinculoId, Long empresaAutenticadaId) {
        if (vinculoId == null) {
            throw new IllegalArgumentException("O ID do vínculo não pode ser nulo.")
        }

        CompetenciaVaga vinculo = repository.findById(vinculoId)
                .orElseThrow(() -> new NoSuchElementException("Vínculo não encontrado."))

        if (empresaAutenticadaId == null || (vinculo.empresaId != null && vinculo.empresaId != empresaAutenticadaId)) {
            throw new AccessDeniedException("Você não tem permissão para remover competências de uma vaga que não pertence à sua empresa.")
        }

        repository.delete(vinculo)
    }
}
