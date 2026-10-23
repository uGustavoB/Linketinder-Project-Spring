package org.ugustavodev.mscompetencia.usecase.impl

import jakarta.transaction.Transactional
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

    private final CompetenciaCandidatoRepository competenciaCandidatoRepository
    private final CompetenciaRepository competenciaRepository
    private final CompetenciaVinculoMapper competenciaVinculoMapper

    VincularCompetenciaCandidatoUseCase(CompetenciaCandidatoRepository competenciaCandidatoRepository, CompetenciaRepository competenciaRepository, CompetenciaVinculoMapper competenciaVinculoMapper) {
        this.competenciaCandidatoRepository = competenciaCandidatoRepository
        this.competenciaRepository = competenciaRepository
        this.competenciaVinculoMapper = competenciaVinculoMapper
    }

    @Override
    @Transactional
    List<CompetenciaVinculoResponseDTO> executar(Long candidatoId, List<VincularCompetenciaRequestDTO> requestDTOs) {
        if (candidatoId == null) {
            throw new IllegalArgumentException("O ID do candidato não pode ser nulo.")
        }
        if (requestDTOs == null) {
            return Collections.emptyList()
        }

        List<CompetenciaVinculoResponseDTO> result = new ArrayList<>()

        for (VincularCompetenciaRequestDTO requestDTO : requestDTOs) {
            Optional<CompetenciaCandidato> vinculoExistente = competenciaCandidatoRepository.findByCandidatoIdAndCompetenciaId(candidatoId, requestDTO.competenciaId)

            CompetenciaCandidato vinculo
            if (vinculoExistente.isPresent()) {
                vinculo = vinculoExistente.get()
                vinculo.nivel = requestDTO.nivel
            } else {
                Competencia competencia = competenciaRepository.findById(requestDTO.competenciaId)
                        .orElseThrow(() -> new NoSuchElementException("Competência com ID ${requestDTO.competenciaId} não encontrada."))

                vinculo = new CompetenciaCandidato()
                vinculo.candidatoId = candidatoId
                vinculo.competencia = competencia
                vinculo.nivel = requestDTO.nivel
            }

            vinculo = competenciaCandidatoRepository.save(vinculo)
            result.add(competenciaVinculoMapper.toResponseDTO(vinculo))
        }

        return result
    }
}
