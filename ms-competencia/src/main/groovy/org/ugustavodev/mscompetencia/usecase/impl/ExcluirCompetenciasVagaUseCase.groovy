package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.ugustavodev.mscompetencia.model.CompetenciaVaga
import org.ugustavodev.mscompetencia.repositories.CompetenciaVagaRepository
import org.ugustavodev.mscompetencia.usecase.interfaces.IExcluirCompetenciasVagaUseCase

@Service
class ExcluirCompetenciasVagaUseCase implements IExcluirCompetenciasVagaUseCase {

    private final CompetenciaVagaRepository repository

    ExcluirCompetenciasVagaUseCase(CompetenciaVagaRepository repository) {
        this.repository = repository
    }

    @Override
    @Transactional
    void executar(Long vagaId, Long empresaAutenticadaId) {
        if (vagaId == null) {
            throw new IllegalArgumentException("O ID da vaga não pode ser nulo.")
        }

        if (empresaAutenticadaId == null) {
            throw new AccessDeniedException("Você não tem permissão para remover competências de uma vaga que não pertence à sua empresa.")
        }

        List<CompetenciaVaga> vinculosExistentes = repository.findByVagaId(vagaId)
        if (!vinculosExistentes.isEmpty() && vinculosExistentes[0].empresaId != null && vinculosExistentes[0].empresaId != empresaAutenticadaId) {
            throw new AccessDeniedException("Você não tem permissão para remover competências de uma vaga que não pertence à sua empresa.")
        }

        repository.deleteByVagaId(vagaId)
    }

}
