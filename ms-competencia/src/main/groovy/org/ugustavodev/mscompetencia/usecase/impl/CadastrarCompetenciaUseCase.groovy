package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.ugustavodev.mscompetencia.dto.request.CompetenciaRequestDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaResponseDTO
import org.ugustavodev.mscompetencia.mapper.CompetenciaMapper
import org.ugustavodev.mscompetencia.model.Competencia
import org.ugustavodev.mscompetencia.repositories.CompetenciaRepository
import org.ugustavodev.mscompetencia.usecase.interfaces.ICadastrarCompetenciaUseCase

@Service
class CadastrarCompetenciaUseCase implements ICadastrarCompetenciaUseCase {
    private final CompetenciaRepository repository
    private final CompetenciaMapper mapper

    CadastrarCompetenciaUseCase(CompetenciaRepository repository, CompetenciaMapper mapper) {
        this.repository = repository
        this.mapper = mapper
    }

    @Override
    @Transactional
    CompetenciaResponseDTO executar(CompetenciaRequestDTO requestDTO) {
        if (requestDTO == null || !requestDTO.nome?.trim()) {
            throw new IllegalArgumentException("O nome da competência é obrigatório.")
        }

        if (repository.existsByNomeIgnoreCase(requestDTO.nome)) {
            throw new IllegalArgumentException("Já existe uma competência com este nome.")
        }

        Competencia competencia = mapper.toEntity(requestDTO)
        competencia = repository.save(competencia)
        return mapper.toResponseDTO(competencia)
    }
}
