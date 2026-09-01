package org.ugustavodev.msnucleo.usecase.impl


import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.request.CandidatoRequestDTO
import org.ugustavodev.msnucleo.dto.response.CandidatoResponseDTO
import org.ugustavodev.msnucleo.mapper.CandidatoMapper
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.usecase.interfaces.ICadastrarCandidatoUseCase

@Service
class CadastrarCandidatoUseCase implements ICadastrarCandidatoUseCase{
    private final CandidatoRepository candidatoRepository

    CadastrarCandidatoUseCase (CandidatoRepository candidatoRepository) {
        this.candidatoRepository = candidatoRepository
    }

    @Override
    CandidatoResponseDTO executar(CandidatoRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("O objeto CandidatoRequestDTO não pode ser nulo.")
        }

        Candidato candidato = CandidatoMapper.fromRequestDTOToEntity(requestDTO)
        Candidato savedCandidato = candidatoRepository.save(candidato)
        return CandidatoMapper.toResponseDTO(savedCandidato)
    }
}
