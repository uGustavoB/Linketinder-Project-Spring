package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.ugustavodev.mscompetencia.dto.request.VincularCompetenciaRequestDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVinculoResponseDTO
import org.ugustavodev.mscompetencia.mapper.CompetenciaVinculoMapper
import org.ugustavodev.mscompetencia.model.Competencia
import org.ugustavodev.mscompetencia.model.CompetenciaEmpresa
import org.ugustavodev.mscompetencia.repositories.CompetenciaEmpresaRepository
import org.ugustavodev.mscompetencia.repositories.CompetenciaRepository
import org.ugustavodev.mscompetencia.usecase.interfaces.IVincularCompetenciaEmpresaUseCase

@Service
class VincularCompetenciaEmpresaUseCase implements IVincularCompetenciaEmpresaUseCase {
    private final CompetenciaEmpresaRepository repository
    private final CompetenciaRepository competenciaRepository
    private final CompetenciaVinculoMapper mapper

    VincularCompetenciaEmpresaUseCase(CompetenciaEmpresaRepository repository, CompetenciaRepository competenciaRepository, CompetenciaVinculoMapper mapper) {
        this.repository = repository
        this.competenciaRepository = competenciaRepository
        this.mapper = mapper
    }

    @Override
    @Transactional
    List<CompetenciaVinculoResponseDTO> executar(Long empresaId, List<VincularCompetenciaRequestDTO> requestDTOs) {
        if (empresaId == null) {
            throw new IllegalArgumentException("O ID da empresa não pode ser nulo.")
        }
        if (requestDTOs == null) {
            return Collections.emptyList()
        }

        List<CompetenciaVinculoResponseDTO> result = new ArrayList<>()

        for (VincularCompetenciaRequestDTO requestDTO : requestDTOs) {
            if (requestDTO == null) {
                throw new IllegalArgumentException("Os dados de vinculação da competência não podem ser nulos.")
            }

            if (requestDTO.competenciaId == null) {
                throw new IllegalArgumentException("O ID da competência é obrigatório.")
            }

            if (requestDTO.nivel == null) {
                throw new IllegalArgumentException("O nível da competência é obrigatório.")
            }

            if (repository.existsByEmpresaIdAndCompetenciaId(empresaId, requestDTO.competenciaId)) {
                continue // Pula caso a empresa já possua esta competência vinculada
            }

            Competencia competencia = competenciaRepository.findById(requestDTO.competenciaId)
                    .orElseThrow(() -> new NoSuchElementException("Competência genérica com ID " + requestDTO.competenciaId + " não encontrada."))

            CompetenciaEmpresa vinculo = new CompetenciaEmpresa()
            vinculo.empresaId = empresaId
            vinculo.competencia = competencia
            vinculo.nivel = requestDTO.nivel

            vinculo = repository.save(vinculo)
            result.add(mapper.toResponseDTO(vinculo))
        }

        return result
    }
}
