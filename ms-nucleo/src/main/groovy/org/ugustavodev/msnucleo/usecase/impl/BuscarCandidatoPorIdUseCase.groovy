package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.response.CandidatoResponseDTO
import org.ugustavodev.msnucleo.mapper.CandidatoMapper
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IBuscarCandidatoPorIdUseCase

@Service
class BuscarCandidatoPorIdUseCase implements IBuscarCandidatoPorIdUseCase {
    private final CandidatoRepository candidatoRepository

    BuscarCandidatoPorIdUseCase(CandidatoRepository candidatoRepository) {
        this.candidatoRepository = candidatoRepository
    }

    @Override
    CandidatoResponseDTO executar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O ID do candidato não pode ser nulo.")
        }

        Candidato candidato = candidatoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Candidato não encontrado."))

        return CandidatoMapper.toResponseDTO(candidato)
    }
}
