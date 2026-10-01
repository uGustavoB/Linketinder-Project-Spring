package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVinculoResponseDTO
import org.ugustavodev.mscompetencia.mapper.CompetenciaVinculoMapper
import org.ugustavodev.mscompetencia.repositories.CompetenciaVagaRepository
import org.ugustavodev.mscompetencia.usecase.interfaces.IListarCompetenciasVagaUseCase

@Service
class ListarCompetenciasVagaUseCase implements IListarCompetenciasVagaUseCase {
    private final CompetenciaVagaRepository repository
    private final CompetenciaVinculoMapper mapper

    ListarCompetenciasVagaUseCase(CompetenciaVagaRepository repository, CompetenciaVinculoMapper mapper) {
        this.repository = repository; this.mapper = mapper
    }

    @Override
    List<CompetenciaVinculoResponseDTO> executar(Long vagaId) {
        return repository.findByVagaId(vagaId).stream().map(mapper::toResponseDTO).toList()
    }
}
