package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.security.access.AccessDeniedException
import org.ugustavodev.mscompetencia.model.CompetenciaCandidato
import org.ugustavodev.mscompetencia.repositories.CompetenciaCandidatoRepository
import spock.lang.Specification
import spock.lang.Subject

class DesvincularCompetenciaCandidatoUseCaseSpec extends Specification {

    CompetenciaCandidatoRepository repository = Mock(CompetenciaCandidatoRepository)

    @Subject
    DesvincularCompetenciaCandidatoUseCase useCase = new DesvincularCompetenciaCandidatoUseCase(repository)

    void "Deve lancar excecao quando vinculoId for nulo"() {
        when: "Executa com vinculoId nulo"
        useCase.executar(null, 1L)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID do vínculo não pode ser nulo."
        0 * repository.delete(_)
    }

    void "Deve lancar NoSuchElementException quando vinculo nao for encontrado"() {
        given: "Um ID de vinculo inexistente"
        Long vinculoId = 99L
        repository.findById(vinculoId) >> Optional.empty()

        when: "Tenta desvincular vinculo inexistente"
        useCase.executar(vinculoId, 1L)

        then: "Lanca NoSuchElementException"
        NoSuchElementException ex = thrown(NoSuchElementException)
        ex.message == "Vínculo não encontrado."
        0 * repository.delete(_)
    }

    void "Deve lancar AccessDeniedException quando candidatoAutenticadoId for nulo"() {
        given: "Um vinculo existente"
        Long vinculoId = 10L
        CompetenciaCandidato vinculo = new CompetenciaCandidato(id: vinculoId, candidatoId: 1L)
        repository.findById(vinculoId) >> Optional.of(vinculo)

        when: "Executa com candidatoAutenticadoId nulo"
        useCase.executar(vinculoId, null)

        then: "Lanca AccessDeniedException"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Você não tem permissão para remover este vínculo."
        0 * repository.delete(_)
    }

    void "Deve lancar AccessDeniedException quando vinculo pertencer a outro candidato (protecao IDOR)"() {
        given: "Um vinculo pertencente ao candidato 2"
        Long vinculoId = 10L
        Long candidatoDonoId = 2L
        Long outroCandidatoId = 1L

        CompetenciaCandidato vinculo = new CompetenciaCandidato(id: vinculoId, candidatoId: candidatoDonoId)
        repository.findById(vinculoId) >> Optional.of(vinculo)

        when: "Candidato 1 tenta deletar vinculo do candidato 2"
        useCase.executar(vinculoId, outroCandidatoId)

        then: "Lanca AccessDeniedException"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Você não tem permissão para remover este vínculo."
        0 * repository.delete(_)
    }

    void "Deve desvincular competencia do candidato com sucesso quando pertencer ao usuario autenticado"() {
        given: "Um vinculo pertencente ao candidato autenticado"
        Long vinculoId = 10L
        Long candidatoId = 1L

        CompetenciaCandidato vinculo = new CompetenciaCandidato(id: vinculoId, candidatoId: candidatoId)
        repository.findById(vinculoId) >> Optional.of(vinculo)

        when: "Executa a desvinculacao"
        useCase.executar(vinculoId, candidatoId)

        then: "Chama o delete do repositorio com o vinculo encontrado"
        1 * repository.delete(vinculo)
    }
}
