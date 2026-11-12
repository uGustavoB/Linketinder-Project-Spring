package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.security.access.AccessDeniedException
import org.ugustavodev.mscompetencia.repositories.CompetenciaCandidatoRepository
import spock.lang.Specification
import spock.lang.Subject

class ExcluirCompetenciasCandidatoUseCaseSpec extends Specification {

    CompetenciaCandidatoRepository repository = Mock(CompetenciaCandidatoRepository)

    @Subject
    ExcluirCompetenciasCandidatoUseCase useCase = new ExcluirCompetenciasCandidatoUseCase(repository)

    void "Deve lancar IllegalArgumentException quando candidatoId for nulo"() {
        when: "Executa com candidatoId nulo"
        useCase.executar(null, 1L)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID do candidato não pode ser nulo."
        0 * repository.deleteByCandidatoId(_)
    }

    void "Deve lancar AccessDeniedException quando candidatoAutenticadoId for nulo"() {
        when: "Executa com candidatoAutenticadoId nulo"
        useCase.executar(1L, null)

        then: "Lanca AccessDeniedException"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Você não tem permissão para remover as competências deste candidato."
        0 * repository.deleteByCandidatoId(_)
    }

    void "Deve lancar AccessDeniedException quando candidatoAutenticadoId for diferente de candidatoId (protecao IDOR)"() {
        when: "Candidato 2 tenta excluir competencias do candidato 1"
        useCase.executar(1L, 2L)

        then: "Lanca AccessDeniedException"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Você não tem permissão para remover as competências deste candidato."
        0 * repository.deleteByCandidatoId(_)
    }

    void "Deve excluir competencias do candidato com sucesso quando for o titular autenticado"() {
        given: "Um ID de candidato autenticado"
        Long candidatoId = 1L

        when: "Executa a exclusao com o titular autenticado"
        useCase.executar(candidatoId, candidatoId)

        then: "Chama o deleteByCandidatoId do repositorio exatamente uma vez"
        1 * repository.deleteByCandidatoId(candidatoId)
    }

}
