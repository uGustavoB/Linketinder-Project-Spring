package org.ugustavodev.msnucleo.usecase.impl

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.response.CurtidaResponseDTO
import org.ugustavodev.msnucleo.mapper.CurtidaMapper
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.model.Curtida
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.repositories.CurtidaRepository
import org.ugustavodev.msnucleo.repositories.VagaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.ICandidatoCurteVagaUseCase

import java.time.LocalDateTime

@Service
class CandidatoCurteVagaUseCase implements ICandidatoCurteVagaUseCase {
    private final CurtidaRepository curtidaRepository
    private final CandidatoRepository candidatoRepository
    private final VagaRepository vagaRepository
    private final CurtidaMapper curtidaMapper

    CandidatoCurteVagaUseCase (CurtidaRepository curtidaRepository, CandidatoRepository candidatoRepository, VagaRepository vagaRepository, CurtidaMapper curtidaMapper) {
        this.curtidaRepository = curtidaRepository
        this.candidatoRepository = candidatoRepository
        this.vagaRepository = vagaRepository
        this.curtidaMapper = curtidaMapper
    }

    @Override
    @Transactional
    CurtidaResponseDTO executar(Long idCandidato, Long idVaga) {
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

        Optional<Curtida> optionalCurtida = curtidaRepository.findByCandidatoIdAndVagaId(idCandidato, idVaga)

        Curtida curtida
        if (optionalCurtida.isPresent()) {
            curtida = optionalCurtida.get()

            if (curtida.getCandidatoCurtiuEm() != null) {
                return curtidaMapper.toResponseDTO(curtida)
            }

            curtida.setCandidatoCurtiuEm(LocalDateTime.now())
        } else {
            curtida = new Curtida()
            curtida.setCandidato(candidato)
            curtida.setVaga(vaga)
            curtida.setCandidatoCurtiuEm(LocalDateTime.now())
        }

        curtida = curtidaRepository.save(curtida)
        return curtidaMapper.toResponseDTO(curtida)
    }
}
