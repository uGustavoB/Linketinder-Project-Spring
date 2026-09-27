package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.response.CurtidaResponseDTO
import org.ugustavodev.msnucleo.mapper.CurtidaMapper
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.model.Curtida
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.repositories.CurtidaRepository
import org.ugustavodev.msnucleo.repositories.VagaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IEmpresaCurteCandidatoUseCase

import java.time.LocalDateTime

@Service
class EmpresaCurteCandidatoUseCase implements IEmpresaCurteCandidatoUseCase{
    private final CurtidaRepository curtidaRepository
    private final CandidatoRepository candidatoRepository
    private final VagaRepository vagaRepository
    private final CurtidaMapper curtidaMapper

    EmpresaCurteCandidatoUseCase(CurtidaRepository curtidaRepository, CandidatoRepository candidatoRepository, VagaRepository vagaRepository, CurtidaMapper curtidaMapper) {
        this.curtidaRepository = curtidaRepository
        this.candidatoRepository = candidatoRepository
        this.vagaRepository = vagaRepository
        this.curtidaMapper = curtidaMapper
    }

    @Override
    CurtidaResponseDTO executar(Long idEmpresa, Long idCandidato, Long idVaga) {
        if (idEmpresa == null) {
            throw new IllegalArgumentException("O ID da empresa não pode ser nulo.")
        }
        if (idCandidato == null) {
            throw new IllegalArgumentException("O ID do candidato não pode ser nulo.")
        }
        if (idVaga == null) {
            throw new IllegalArgumentException("O ID da vaga não pode ser nulo.")
        }

        Candidato candidato = candidatoRepository.findById(idCandidato)
                .orElseThrow(() -> new NoSuchElementException("Candidato não encontrado para o ID: ${idCandidato}"))

        Vaga vaga = vagaRepository.findById(idVaga)
                .orElseThrow(() -> new NoSuchElementException("Vaga não encontrada para o ID: ${idVaga}"))

        if (vaga.getEmpresa().getId() != idEmpresa) {
            throw new AccessDeniedException("Esta vaga não pertence à sua empresa.")
        }

        Optional<Curtida> optionalCurtida = curtidaRepository.findByCandidatoIdAndVagaId(idCandidato, idVaga)

        Curtida curtida
        if (optionalCurtida.isPresent()) {
            curtida = optionalCurtida.get()

            if (curtida.getEmpresaCurtiuEm() != null) {
                throw new IllegalStateException("A empresa já curtiu esta vaga.")
            }

            curtida.setEmpresaCurtiuEm(LocalDateTime.now())
        } else {
            curtida = new Curtida()
            curtida.setCandidato(candidato)
            curtida.setVaga(vaga)
            curtida.setEmpresaCurtiuEm(LocalDateTime.now())
        }

        curtida = curtidaRepository.save(curtida)
        return curtidaMapper.toResponseDTO(curtida)
    }
}
