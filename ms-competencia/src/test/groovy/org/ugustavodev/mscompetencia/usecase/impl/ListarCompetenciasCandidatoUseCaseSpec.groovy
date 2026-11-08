package org.ugustavodev.mscompetencia.usecase.impl

import org.ugustavodev.mscompetencia.dto.response.CompetenciaResponseDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVinculoResponseDTO
import org.ugustavodev.mscompetencia.mapper.CompetenciaVinculoMapper
import org.ugustavodev.mscompetencia.model.Competencia
import org.ugustavodev.mscompetencia.model.CompetenciaCandidato
import org.ugustavodev.mscompetencia.model.enums.NivelCompetencia
import org.ugustavodev.mscompetencia.repositories.CompetenciaCandidatoRepository
import spock.lang.Specification
import spock.lang.Subject

class ListarCompetenciasCandidatoUseCaseSpec extends Specification {

    CompetenciaCandidatoRepository repository = Mock(CompetenciaCandidatoRepository)
    CompetenciaVinculoMapper mapper = Mock(CompetenciaVinculoMapper)

    @Subject
    ListarCompetenciasCandidatoUseCase useCase = new ListarCompetenciasCandidatoUseCase(repository, mapper)

    void "Deve lancar excecao quando candidatoId for nulo"() {
        when: "Executa com candidatoId nulo"
        useCase.executar(null)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID do candidato não pode ser nulo."
    }

    void "Deve retornar lista de competencias do candidato com sucesso"() {
        given: "Um candidato com vinculos cadastrados"
        Long candidatoId = 1L
        Competencia comp1 = new Competencia(id: 1L, nome: "Java")
        CompetenciaCandidato vinculo = new CompetenciaCandidato(id: 10L, candidatoId: candidatoId, competencia: comp1, nivel: NivelCompetencia.AVANCADO)
        CompetenciaVinculoResponseDTO dto = new CompetenciaVinculoResponseDTO(10L, new CompetenciaResponseDTO(1L, "Java"), NivelCompetencia.AVANCADO)

        repository.findByCandidatoId(candidatoId) >> [vinculo]
        mapper.toResponseDTO(vinculo) >> dto

        when: "Executa a listagem"
        List<CompetenciaVinculoResponseDTO> result = useCase.executar(candidatoId)

        then: "Retorna a lista mapeada"
        result.size() == 1
        result[0].idVinculo == 10L
        result[0].competencia.nome == "Java"
        result[0].nivel == NivelCompetencia.AVANCADO
    }
}
