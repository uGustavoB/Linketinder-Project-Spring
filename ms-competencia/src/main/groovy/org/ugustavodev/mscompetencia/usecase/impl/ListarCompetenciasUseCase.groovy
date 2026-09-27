package org.ugustavodev.mscompetencia.usecase.impl
import org.springframework.stereotype.Service
import org.ugustavodev.mscompetencia.dto.response.CompetenciaResponseDTO
import org.ugustavodev.mscompetencia.mapper.CompetenciaMapper
import org.ugustavodev.mscompetencia.repositories.CompetenciaRepository
import org.ugustavodev.mscompetencia.usecase.interfaces.IListarCompetenciasUseCase

@Service
class ListarCompetenciasUseCase implements IListarCompetenciasUseCase {
    private final CompetenciaRepository repository
    private final CompetenciaMapper mapper

    ListarCompetenciasUseCase(CompetenciaRepository repository, CompetenciaMapper mapper) {
        this.repository = repository; this.mapper = mapper
    }

    @Override
    List<CompetenciaResponseDTO> executar() {
        return repository.findAll().stream().map(mapper::toResponseDTO).toList()
    }
}
