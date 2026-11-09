package org.ugustavodev.mscompetencia.usecase.impl

import org.ugustavodev.mscompetencia.dto.response.CompetenciaResponseDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVagaVinculoResponseDTO
import org.ugustavodev.mscompetencia.mapper.CompetenciaVinculoMapper
import org.ugustavodev.mscompetencia.model.Competencia
import org.ugustavodev.mscompetencia.model.CompetenciaVaga
import org.ugustavodev.mscompetencia.repositories.CompetenciaVagaRepository
import spock.lang.Specification
import spock.lang.Subject

class ListarCompetenciasVagaUseCaseSpec extends Specification {

    CompetenciaVagaRepository repository = Mock(CompetenciaVagaRepository)
    CompetenciaVinculoMapper mapper = Mock(CompetenciaVinculoMapper)

    @Subject
    ListarCompetenciasVagaUseCase useCase = new ListarCompetenciasVagaUseCase(repository, mapper)

    void "Deve lancar excecao quando vagaId for nulo"() {
        when: "Executa com vagaId nulo"
        useCase.executar(null)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID da vaga não pode ser nulo."
    }

    void "Deve retornar lista de competencias da vaga com sucesso"() {
        given: "Uma vaga com vinculos cadastrados"
        Long vagaId = 10L
        Competencia comp1 = new Competencia(id: 1L, nome: "Java")
        CompetenciaVaga vinculo = new CompetenciaVaga(id: 100L, vagaId: vagaId, competencia: comp1)
        CompetenciaVagaVinculoResponseDTO dto = new CompetenciaVagaVinculoResponseDTO(100L, new CompetenciaResponseDTO(1L, "Java"))

        repository.findByVagaId(vagaId) >> [vinculo]
        mapper.toVagaResponseDTO(vinculo) >> dto

        when: "Executa a listagem"
        List<CompetenciaVagaVinculoResponseDTO> result = useCase.executar(vagaId)

        then: "Retorna a lista mapeada"
        result.size() == 1
        result[0].idVinculo == 100L
        result[0].competencia.nome == "Java"
    }
}
