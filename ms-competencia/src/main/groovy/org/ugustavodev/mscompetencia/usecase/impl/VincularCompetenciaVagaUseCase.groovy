package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.ugustavodev.mscompetencia.dto.request.VincularCompetenciaVagaRequestDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVagaVinculoResponseDTO
import org.ugustavodev.mscompetencia.mapper.CompetenciaVinculoMapper
import org.ugustavodev.mscompetencia.model.Competencia
import org.ugustavodev.mscompetencia.model.CompetenciaVaga
import org.ugustavodev.mscompetencia.repositories.CompetenciaRepository
import org.ugustavodev.mscompetencia.repositories.CompetenciaVagaRepository
import org.ugustavodev.mscompetencia.usecase.interfaces.IVincularCompetenciaVagaUseCase

@Service
class VincularCompetenciaVagaUseCase implements IVincularCompetenciaVagaUseCase {
    private final CompetenciaVagaRepository repository
    private final CompetenciaRepository competenciaRepository
    private final CompetenciaVinculoMapper mapper

    VincularCompetenciaVagaUseCase(CompetenciaVagaRepository repository, CompetenciaRepository competenciaRepository, CompetenciaVinculoMapper mapper) {
        this.repository = repository
        this.competenciaRepository = competenciaRepository
        this.mapper = mapper
    }

    @Override
    @Transactional
    List<CompetenciaVagaVinculoResponseDTO> executar(Long vagaId, Long empresaId, List<VincularCompetenciaVagaRequestDTO> requestDTOs) {
        if (vagaId == null) {
            throw new IllegalArgumentException("O ID da vaga não pode ser nulo.")
        }
        if (empresaId == null) {
            throw new AccessDeniedException("Você não tem permissão para vincular competências a esta vaga.")
        }
        if (requestDTOs == null) {
            return Collections.emptyList()
        }

        List<CompetenciaVaga> vinculosExistentes = repository.findByVagaId(vagaId)
        if (!vinculosExistentes.isEmpty() && vinculosExistentes[0].empresaId != null && vinculosExistentes[0].empresaId != empresaId) {
            throw new AccessDeniedException("Você não tem permissão para alterar as competências de uma vaga de outra empresa.")
        }

        List<CompetenciaVagaVinculoResponseDTO> result = new ArrayList<>()

        for (VincularCompetenciaVagaRequestDTO requestDTO : requestDTOs) {
            if (requestDTO == null) {
                throw new IllegalArgumentException("Os dados de vinculação da competência não podem ser nulos.")
            }

            if (requestDTO.competenciaId == null) {
                throw new IllegalArgumentException("O ID da competência é obrigatório.")
            }

            if (repository.existsByVagaIdAndCompetenciaId(vagaId, requestDTO.competenciaId)) {
                continue // Pula caso a vaga já possua esta competência vinculada
            }

            Competencia competencia = competenciaRepository.findById(requestDTO.competenciaId)
                    .orElseThrow(() -> new NoSuchElementException("Competência genérica com ID " + requestDTO.competenciaId + " não encontrada."))

            CompetenciaVaga vinculo = new CompetenciaVaga()
            vinculo.vagaId = vagaId
            vinculo.empresaId = empresaId
            vinculo.competencia = competencia

            vinculo = repository.save(vinculo)
            result.add(mapper.toVagaResponseDTO(vinculo))
        }

        return result
    }
}
