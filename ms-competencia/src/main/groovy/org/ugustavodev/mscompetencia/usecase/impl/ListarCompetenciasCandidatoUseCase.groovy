package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVinculoResponseDTO
import org.ugustavodev.mscompetencia.mapper.CompetenciaVinculoMapper
import org.ugustavodev.mscompetencia.repositories.CompetenciaCandidatoRepository
import org.ugustavodev.mscompetencia.usecase.interfaces.IListarCompetenciasCandidatoUseCase

@Service
class ListarCompetenciasCandidatoUseCase implements IListarCompetenciasCandidatoUseCase {
    private final CompetenciaCandidatoRepository repository
    private final CompetenciaVinculoMapper mapper

    ListarCompetenciasCandidatoUseCase(CompetenciaCandidatoRepository repository, CompetenciaVinculoMapper mapper) {
        this.repository = repository
        this.mapper = mapper
    }

    @Override
    @Transactional(readOnly = true)
    List<CompetenciaVinculoResponseDTO> executar(Long candidatoId) {
        if (candidatoId == null) {
            throw new IllegalArgumentException("O ID do candidato não pode ser nulo.")
        }

        return repository.findByCandidatoId(candidatoId).stream().map(mapper::toResponseDTO).toList()
    }
}
