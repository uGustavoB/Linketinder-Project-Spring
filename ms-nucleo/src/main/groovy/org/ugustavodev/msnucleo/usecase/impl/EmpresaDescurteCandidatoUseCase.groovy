package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.model.Curtida
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.CurtidaRepository
import org.ugustavodev.msnucleo.repositories.VagaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IEmpresaDescurteCandidatoUseCase

import java.util.NoSuchElementException

@Service
class EmpresaDescurteCandidatoUseCase implements IEmpresaDescurteCandidatoUseCase {
    private final CurtidaRepository curtidaRepository
    private final VagaRepository vagaRepository

    EmpresaDescurteCandidatoUseCase(CurtidaRepository curtidaRepository, VagaRepository vagaRepository) {
        this.curtidaRepository = curtidaRepository
        this.vagaRepository = vagaRepository
    }

    @Override
    void executar(Long idEmpresa, Long idCandidato, Long idVaga) {
        if (idEmpresa == null) {
            throw new IllegalArgumentException("O ID da empresa não pode ser nulo.")
        }
        if (idCandidato == null) {
            throw new IllegalArgumentException("O ID do candidato não pode ser nulo.")
        }
        if (idVaga == null) {
            throw new IllegalArgumentException("O ID da vaga não pode ser nulo.")
        }

        Vaga vaga = vagaRepository.findById(idVaga)
                .orElseThrow(() -> new NoSuchElementException("Vaga não encontrada."))

        if (vaga.getEmpresa().getId() != idEmpresa) {
            throw new AccessDeniedException("Esta vaga não pertence à sua empresa.")
        }

        Curtida curtida = curtidaRepository.findByCandidatoIdAndVagaId(idCandidato, idVaga)
                .orElseThrow(() -> new NoSuchElementException("Curtida não encontrada."))

        if (curtida.getEmpresaCurtiuEm() == null) {
            throw new IllegalStateException("A empresa não curtiu este candidato para esta vaga.")
        }

        if (curtida.getCandidatoCurtiuEm() != null) {
            curtida.setEmpresaCurtiuEm(null)
            curtidaRepository.save(curtida)
        } else {
            curtidaRepository.delete(curtida)
        }
    }
}
