package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.response.CandidatoResponseDTO
import org.ugustavodev.msnucleo.mapper.CandidatoMapper
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IListarCandidatosUseCase

@Service
class ListarCandidatosUseCase implements IListarCandidatosUseCase {
    private final CandidatoRepository candidatoRepository

    ListarCandidatosUseCase(CandidatoRepository candidatoRepository) {
        this.candidatoRepository = candidatoRepository
    }

    @Override
    List<CandidatoResponseDTO> executar() {
        return candidatoRepository.findAll()
                .collect { CandidatoMapper.toResponseDTO(it) }
    }
}
