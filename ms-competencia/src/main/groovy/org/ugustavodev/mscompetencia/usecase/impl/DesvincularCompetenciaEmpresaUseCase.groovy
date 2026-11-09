package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.ugustavodev.mscompetencia.model.CompetenciaEmpresa
import org.ugustavodev.mscompetencia.repositories.CompetenciaEmpresaRepository
import org.ugustavodev.mscompetencia.usecase.interfaces.IDesvincularCompetenciaEmpresaUseCase

@Service
class DesvincularCompetenciaEmpresaUseCase implements IDesvincularCompetenciaEmpresaUseCase {

    private final CompetenciaEmpresaRepository repository

    DesvincularCompetenciaEmpresaUseCase(CompetenciaEmpresaRepository repository) {
        this.repository = repository
    }

    @Override
    @Transactional
    void executar(Long vinculoId, Long empresaAutenticadaId) {
        if (vinculoId == null) {
            throw new IllegalArgumentException("O ID do vínculo não pode ser nulo.")
        }

        CompetenciaEmpresa vinculo = repository.findById(vinculoId)
                .orElseThrow { new NoSuchElementException("Vínculo não encontrado.") }

        if (empresaAutenticadaId == null || vinculo.empresaId != empresaAutenticadaId) {
            throw new AccessDeniedException("Você não tem permissão para remover este vínculo.")
        }

        repository.delete(vinculo)
    }

}
