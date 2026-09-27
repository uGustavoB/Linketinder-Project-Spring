package org.ugustavodev.mscompetencia.mapper

import org.springframework.stereotype.Component
import org.ugustavodev.mscompetencia.dto.request.CompetenciaRequestDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaResponseDTO
import org.ugustavodev.mscompetencia.model.Competencia

@Component
class CompetenciaMapper {
    CompetenciaResponseDTO toResponseDTO(Competencia competencia) {
        return new CompetenciaResponseDTO(competencia.id, competencia.nome)
    }

    Competencia toEntity(CompetenciaRequestDTO requestDTO) {
        Competencia competencia = new Competencia()
        competencia.nome = requestDTO.nome
        return competencia
    }
}
