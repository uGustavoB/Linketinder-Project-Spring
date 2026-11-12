package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.ugustavodev.mscompetencia.repositories.CompetenciaEmpresaRepository
import org.ugustavodev.mscompetencia.repositories.CompetenciaVagaRepository
import org.ugustavodev.mscompetencia.usecase.interfaces.IExcluirCompetenciasEmpresaUseCase

@Service
class ExcluirCompetenciasEmpresaUseCase implements IExcluirCompetenciasEmpresaUseCase {

    private final CompetenciaEmpresaRepository competenciaEmpresaRepository
    private final CompetenciaVagaRepository competenciaVagaRepository

    ExcluirCompetenciasEmpresaUseCase(
            CompetenciaEmpresaRepository competenciaEmpresaRepository,
            CompetenciaVagaRepository competenciaVagaRepository) {
        this.competenciaEmpresaRepository = competenciaEmpresaRepository
        this.competenciaVagaRepository = competenciaVagaRepository
    }

    @Override
    @Transactional
    void executar(Long empresaId, Long empresaAutenticadaId) {
        if (empresaId == null) {
            throw new IllegalArgumentException("O ID da empresa não pode ser nulo.")
        }

        if (empresaAutenticadaId == null || empresaAutenticadaId != empresaId) {
            throw new AccessDeniedException("Você não tem permissão para remover as competências desta empresa.")
        }

        competenciaEmpresaRepository.deleteByEmpresaId(empresaId)
        competenciaVagaRepository.deleteByEmpresaId(empresaId)
    }

}
