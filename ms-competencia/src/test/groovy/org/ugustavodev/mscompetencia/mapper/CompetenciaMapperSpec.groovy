package org.ugustavodev.mscompetencia.mapper

import org.ugustavodev.mscompetencia.dto.request.CompetenciaRequestDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaResponseDTO
import org.ugustavodev.mscompetencia.model.Competencia
import spock.lang.Specification
import spock.lang.Subject

class CompetenciaMapperSpec extends Specification {

    @Subject
    CompetenciaMapper mapper = new CompetenciaMapper()

    void "Deve converter entidade Competencia para CompetenciaResponseDTO"() {
        given: "Uma entidade Competencia"
        Competencia entidade = new Competencia(id: 1L, nome: "Java")

        when: "Converte para DTO"
        CompetenciaResponseDTO dto = mapper.toResponseDTO(entidade)

        then: "Os dados sao mapeados corretamente"
        dto.id == 1L
        dto.nome == "Java"
    }

    void "Deve converter CompetenciaRequestDTO para entidade Competencia"() {
        given: "Um CompetenciaRequestDTO"
        CompetenciaRequestDTO dto = new CompetenciaRequestDTO(nome: "TypeScript")

        when: "Converte para entidade"
        Competencia entidade = mapper.toEntity(dto)

        then: "O nome e mapeado corretamente"
        entidade.nome == "TypeScript"
        entidade.id == null
    }
}
