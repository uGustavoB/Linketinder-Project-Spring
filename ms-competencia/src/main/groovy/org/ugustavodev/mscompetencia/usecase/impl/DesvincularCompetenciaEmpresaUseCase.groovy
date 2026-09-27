package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.stereotype.Service
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
    void executar(Long vinculoId) {
        CompetenciaEmpresa vinculo = repository.findById(vinculoId)
                .orElseThrow(() -> new NoSuchElementException("Vínculo não encontrado."))
        repository.delete(vinculo)
    }
}
