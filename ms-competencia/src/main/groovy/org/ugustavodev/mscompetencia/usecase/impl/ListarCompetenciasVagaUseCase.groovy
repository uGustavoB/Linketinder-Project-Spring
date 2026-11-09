package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVagaVinculoResponseDTO
import org.ugustavodev.mscompetencia.mapper.CompetenciaVinculoMapper
import org.ugustavodev.mscompetencia.repositories.CompetenciaVagaRepository
import org.ugustavodev.mscompetencia.usecase.interfaces.IListarCompetenciasVagaUseCase

@Service
class ListarCompetenciasVagaUseCase implements IListarCompetenciasVagaUseCase {
    private final CompetenciaVagaRepository repository
    private final CompetenciaVinculoMapper mapper

    ListarCompetenciasVagaUseCase(CompetenciaVagaRepository repository, CompetenciaVinculoMapper mapper) {
        this.repository = repository
        this.mapper = mapper
    }

    @Override
    @Transactional(readOnly = true)
    List<CompetenciaVagaVinculoResponseDTO> executar(Long vagaId) {
        if (vagaId == null) {
            throw new IllegalArgumentException("O ID da vaga não pode ser nulo.")
        }

        return repository.findByVagaId(vagaId).stream().map(mapper::toVagaResponseDTO).toList()
    }
}
