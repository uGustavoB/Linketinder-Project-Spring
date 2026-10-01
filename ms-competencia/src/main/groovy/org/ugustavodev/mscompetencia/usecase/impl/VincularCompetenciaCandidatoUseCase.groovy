package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.mscompetencia.dto.request.VincularCompetenciaRequestDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVinculoResponseDTO
import org.ugustavodev.mscompetencia.mapper.CompetenciaVinculoMapper
import org.ugustavodev.mscompetencia.model.Competencia
import org.ugustavodev.mscompetencia.model.CompetenciaCandidato
import org.ugustavodev.mscompetencia.repositories.CompetenciaCandidatoRepository
import org.ugustavodev.mscompetencia.repositories.CompetenciaRepository
import org.ugustavodev.mscompetencia.usecase.interfaces.IVincularCompetenciaCandidatoUseCase

@Service
class VincularCompetenciaCandidatoUseCase implements IVincularCompetenciaCandidatoUseCase {
    private final CompetenciaCandidatoRepository repository
    private final CompetenciaRepository competenciaRepository
    private final CompetenciaVinculoMapper mapper

    VincularCompetenciaCandidatoUseCase(CompetenciaCandidatoRepository repository, CompetenciaRepository competenciaRepository, CompetenciaVinculoMapper mapper) {
        this.repository = repository; this.competenciaRepository = competenciaRepository; this.mapper = mapper
    }

    @Override
    List<CompetenciaVinculoResponseDTO> executar(Long candidatoId, List<VincularCompetenciaRequestDTO> requestDTOs) {
        List<CompetenciaVinculoResponseDTO> result = new ArrayList<>()

        for (VincularCompetenciaRequestDTO requestDTO : requestDTOs) {
            if (repository.existsByCandidatoIdAndCompetenciaId(candidatoId, requestDTO.competenciaId)) {
                continue // Pula caso já tenha vínculo com o candidato
            }
            
            Competencia competencia = competenciaRepository.findById(requestDTO.competenciaId)
                    .orElseThrow(() -> new NoSuchElementException("Competência genérica com ID " + requestDTO.competenciaId + " não encontrada."))

            CompetenciaCandidato vinculo = new CompetenciaCandidato()
            vinculo.candidatoId = candidatoId
            vinculo.competencia = competencia
            vinculo.nivel = requestDTO.nivel

            vinculo = repository.save(vinculo)
            result.add(mapper.toResponseDTO(vinculo))
        }

        return result
    }
}
