package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.mscompetencia.dto.request.VincularCompetenciaRequestDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVinculoResponseDTO
import org.ugustavodev.mscompetencia.mapper.CompetenciaVinculoMapper
import org.ugustavodev.mscompetencia.model.Competencia
import org.ugustavodev.mscompetencia.model.CompetenciaVaga
import org.ugustavodev.mscompetencia.repositories.CompetenciaVagaRepository
import org.ugustavodev.mscompetencia.repositories.CompetenciaRepository
import org.ugustavodev.mscompetencia.usecase.interfaces.IVincularCompetenciaVagaUseCase

@Service
class VincularCompetenciaVagaUseCase implements IVincularCompetenciaVagaUseCase {
    private final CompetenciaVagaRepository repository
    private final CompetenciaRepository competenciaRepository
    private final CompetenciaVinculoMapper mapper

    VincularCompetenciaVagaUseCase(CompetenciaVagaRepository repository, CompetenciaRepository competenciaRepository, CompetenciaVinculoMapper mapper) {
        this.repository = repository; this.competenciaRepository = competenciaRepository; this.mapper = mapper
    }

    @Override
    CompetenciaVinculoResponseDTO executar(Long vagaId, VincularCompetenciaRequestDTO requestDTO) {
        if (repository.existsByVagaIdAndCompetenciaId(vagaId, requestDTO.competenciaId)) {
            throw new IllegalArgumentException("A vaga já possui esta competência vinculada.")
        }
        Competencia competencia = competenciaRepository.findById(requestDTO.competenciaId)
                .orElseThrow(() -> new NoSuchElementException("Competência genérica não encontrada."))

        CompetenciaVaga vinculo = new CompetenciaVaga()
        vinculo.vagaId = vagaId
        vinculo.competencia = competencia
        vinculo.nivel = requestDTO.nivel

        vinculo = repository.save(vinculo)
        return mapper.toResponseDTO(vinculo)
    }
}
