package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVinculoResponseDTO
import org.ugustavodev.mscompetencia.mapper.CompetenciaVinculoMapper
import org.ugustavodev.mscompetencia.repositories.CompetenciaCandidatoRepository
import org.ugustavodev.mscompetencia.usecase.interfaces.IListarCompetenciasCandidatoUseCase

@Service
class ListarCompetenciasCandidatoUseCase implements IListarCompetenciasCandidatoUseCase {
    private final CompetenciaCandidatoRepository repository
    private final CompetenciaVinculoMapper mapper

    ListarCompetenciasCandidatoUseCase(CompetenciaCandidatoRepository repository, CompetenciaVinculoMapper mapper) {
        this.repository = repository; this.mapper = mapper
    }

    @Override
    List<CompetenciaVinculoResponseDTO> executar(Long candidatoId) {
        return repository.findByCandidatoId(candidatoId).stream().map(mapper::toResponseDTO).toList()
    }
}
