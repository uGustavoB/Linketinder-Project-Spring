package org.ugustavodev.mscompetencia.mapper

import org.springframework.stereotype.Component
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVinculoResponseDTO
import org.ugustavodev.mscompetencia.model.CompetenciaCandidato
import org.ugustavodev.mscompetencia.model.CompetenciaEmpresa

@Component
class CompetenciaVinculoMapper {
    private final CompetenciaMapper competenciaMapper

    CompetenciaVinculoMapper(CompetenciaMapper competenciaMapper) {
        this.competenciaMapper = competenciaMapper
    }

    CompetenciaVinculoResponseDTO toResponseDTO(CompetenciaCandidato vinculo) {
        return new CompetenciaVinculoResponseDTO(
                vinculo.id,
                competenciaMapper.toResponseDTO(vinculo.competencia),
                vinculo.nivel
        )
    }

    CompetenciaVinculoResponseDTO toResponseDTO(CompetenciaEmpresa vinculo) {
        return new CompetenciaVinculoResponseDTO(
                vinculo.id,
                competenciaMapper.toResponseDTO(vinculo.competencia),
                vinculo.nivel
        )
    }
}
