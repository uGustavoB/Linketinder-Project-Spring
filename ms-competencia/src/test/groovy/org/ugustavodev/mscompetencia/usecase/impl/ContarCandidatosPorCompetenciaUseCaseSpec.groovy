package org.ugustavodev.mscompetencia.usecase.impl

import org.ugustavodev.mscompetencia.dto.response.CandidatosPorCompetenciaResponseDTO
import org.ugustavodev.mscompetencia.repositories.CompetenciaCandidatoRepository
import spock.lang.Specification
import spock.lang.Subject

class ContarCandidatosPorCompetenciaUseCaseSpec extends Specification {

    CompetenciaCandidatoRepository repository = Mock(CompetenciaCandidatoRepository)

    @Subject
    ContarCandidatosPorCompetenciaUseCase useCase = new ContarCandidatosPorCompetenciaUseCase(repository)

    void "Deve retornar a contagem de candidatos agrupada por competencia com sucesso"() {
        given: "Estatisticas retornadas pelo repositorio ordenadas por total de candidatos"
        List<CandidatosPorCompetenciaResponseDTO> estatisticasEsperadas = [
                new CandidatosPorCompetenciaResponseDTO(1L, "Java", 12L),
                new CandidatosPorCompetenciaResponseDTO(2L, "Spring Boot", 9L),
                new CandidatosPorCompetenciaResponseDTO(3L, "Angular", 0L)
        ]
        repository.contarCandidatosPorCompetencia() >> estatisticasEsperadas

        when: "Executa a contagem de candidatos por competencia"
        List<CandidatosPorCompetenciaResponseDTO> resultado = useCase.executar()

        then: "Retorna a lista de estatisticas exatamente como agregada pelo banco"
        resultado.size() == 3
        resultado[0].competenciaId() == 1L
        resultado[0].nomeCompetencia() == "Java"
        resultado[0].totalCandidatos() == 12L
        resultado[1].nomeCompetencia() == "Spring Boot"
        resultado[1].totalCandidatos() == 9L
        resultado[2].nomeCompetencia() == "Angular"
        resultado[2].totalCandidatos() == 0L
    }

    void "Deve retornar lista vazia quando nao houver competencias cadastradas"() {
        given: "Nenhuma competencia no catalogo"
        repository.contarCandidatosPorCompetencia() >> []

        when: "Executa a contagem"
        List<CandidatosPorCompetenciaResponseDTO> resultado = useCase.executar()

        then: "Retorna lista vazia sem erros"
        resultado.isEmpty()
    }
}
