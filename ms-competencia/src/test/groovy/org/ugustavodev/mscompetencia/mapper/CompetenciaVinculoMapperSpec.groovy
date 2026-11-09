package org.ugustavodev.mscompetencia.mapper

import org.ugustavodev.mscompetencia.dto.response.CompetenciaResponseDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVagaVinculoResponseDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVinculoResponseDTO
import org.ugustavodev.mscompetencia.model.Competencia
import org.ugustavodev.mscompetencia.model.CompetenciaCandidato
import org.ugustavodev.mscompetencia.model.CompetenciaEmpresa
import org.ugustavodev.mscompetencia.model.CompetenciaVaga
import org.ugustavodev.mscompetencia.model.enums.NivelCompetencia
import spock.lang.Specification
import spock.lang.Subject

class CompetenciaVinculoMapperSpec extends Specification {

    CompetenciaMapper competenciaMapper = Mock(CompetenciaMapper)

    @Subject
    CompetenciaVinculoMapper mapper = new CompetenciaVinculoMapper(competenciaMapper)

    void "Deve converter CompetenciaCandidato para CompetenciaVinculoResponseDTO"() {
        given: "Um vinculo de candidato"
        Competencia comp = new Competencia(id: 1L, nome: "Java")
        CompetenciaCandidato vinculo = new CompetenciaCandidato(id: 10L, candidatoId: 2L, competencia: comp, nivel: NivelCompetencia.AVANCADO)
        competenciaMapper.toResponseDTO(comp) >> new CompetenciaResponseDTO(1L, "Java")

        when: "Converte para DTO"
        CompetenciaVinculoResponseDTO dto = mapper.toResponseDTO(vinculo)

        then: "Os dados sao mapeados corretamente"
        dto.idVinculo == 10L
        dto.competencia.nome == "Java"
        dto.nivel == NivelCompetencia.AVANCADO
    }

    void "Deve converter CompetenciaEmpresa para CompetenciaVinculoResponseDTO"() {
        given: "Um vinculo de empresa"
        Competencia comp = new Competencia(id: 2L, nome: "Spring Boot")
        CompetenciaEmpresa vinculo = new CompetenciaEmpresa(id: 20L, empresaId: 3L, competencia: comp, nivel: NivelCompetencia.MEDIO)
        competenciaMapper.toResponseDTO(comp) >> new CompetenciaResponseDTO(2L, "Spring Boot")

        when: "Converte para DTO"
        CompetenciaVinculoResponseDTO dto = mapper.toResponseDTO(vinculo)

        then: "Os dados sao mapeados corretamente"
        dto.idVinculo == 20L
        dto.competencia.nome == "Spring Boot"
        dto.nivel == NivelCompetencia.MEDIO
    }

    void "Deve converter CompetenciaVaga para CompetenciaVagaVinculoResponseDTO"() {
        given: "Um vinculo de vaga"
        Competencia comp = new Competencia(id: 3L, nome: "Docker")
        CompetenciaVaga vinculo = new CompetenciaVaga(id: 30L, vagaId: 5L, empresaId: 3L, competencia: comp)
        competenciaMapper.toResponseDTO(comp) >> new CompetenciaResponseDTO(3L, "Docker")

        when: "Converte para DTO"
        CompetenciaVagaVinculoResponseDTO dto = mapper.toVagaResponseDTO(vinculo)

        then: "Os dados sao mapeados corretamente"
        dto.idVinculo == 30L
        dto.competencia.nome == "Docker"
    }
}
