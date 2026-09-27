package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVinculoResponseDTO
import org.ugustavodev.mscompetencia.mapper.CompetenciaVinculoMapper
import org.ugustavodev.mscompetencia.repositories.CompetenciaEmpresaRepository
import org.ugustavodev.mscompetencia.usecase.interfaces.IListarCompetenciasEmpresaUseCase

@Service
class ListarCompetenciasEmpresaUseCase implements IListarCompetenciasEmpresaUseCase {
    private final CompetenciaEmpresaRepository repository
    private final CompetenciaVinculoMapper mapper

    ListarCompetenciasEmpresaUseCase(CompetenciaEmpresaRepository repository, CompetenciaVinculoMapper mapper) {
        this.repository = repository; this.mapper = mapper
    }

    @Override
    List<CompetenciaVinculoResponseDTO> executar(Long empresaId) {
        return repository.findByEmpresaId(empresaId).stream().map(mapper::toResponseDTO).toList()
    }
}
