package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.response.CandidatoResponseDTO
import org.ugustavodev.msnucleo.mapper.CandidatoMapper
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IListarCandidatosUseCase

@Service
class ListarCandidatosUseCase implements IListarCandidatosUseCase {
    private final CandidatoRepository candidatoRepository
    private final CandidatoMapper candidatoMapper

    ListarCandidatosUseCase(CandidatoRepository candidatoRepository, CandidatoMapper candidatoMapper) {
        this.candidatoRepository = candidatoRepository
        this.candidatoMapper = candidatoMapper
    }


    @Override
    List<CandidatoResponseDTO> executar() {
        return candidatoRepository.findAll()
                .collect { candidatoMapper.toResponseDTO(it) }
    }
}
