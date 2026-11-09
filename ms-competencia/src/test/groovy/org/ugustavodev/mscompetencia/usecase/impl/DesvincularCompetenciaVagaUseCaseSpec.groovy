package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.security.access.AccessDeniedException
import org.ugustavodev.mscompetencia.model.CompetenciaVaga
import org.ugustavodev.mscompetencia.repositories.CompetenciaVagaRepository
import spock.lang.Specification
import spock.lang.Subject

class DesvincularCompetenciaVagaUseCaseSpec extends Specification {

    CompetenciaVagaRepository repository = Mock(CompetenciaVagaRepository)

    @Subject
    DesvincularCompetenciaVagaUseCase useCase = new DesvincularCompetenciaVagaUseCase(repository)

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

    void "Deve lancar AccessDeniedException quando empresaAutenticadaId for nulo"() {
        given: "Um vinculo existente"
        Long vinculoId = 10L
        CompetenciaVaga vinculo = new CompetenciaVaga(id: vinculoId, empresaId: 1L)
        repository.findById(vinculoId) >> Optional.of(vinculo)

        when: "Executa com empresaAutenticadaId nulo"
        useCase.executar(vinculoId, null)

        then: "Lanca AccessDeniedException"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Você não tem permissão para remover competências de uma vaga que não pertence à sua empresa."
        0 * repository.delete(_)
    }

    void "Deve lancar AccessDeniedException quando vinculo pertencer a outra empresa (protecao IDOR)"() {
        given: "Um vinculo pertencente a empresa 2"
        Long vinculoId = 10L
        Long empresaDonaId = 2L
        Long outraEmpresaId = 1L

        CompetenciaVaga vinculo = new CompetenciaVaga(id: vinculoId, empresaId: empresaDonaId)
        repository.findById(vinculoId) >> Optional.of(vinculo)

        when: "Empresa 1 tenta deletar vinculo da empresa 2"
        useCase.executar(vinculoId, outraEmpresaId)

        then: "Lanca AccessDeniedException"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Você não tem permissão para remover competências de uma vaga que não pertence à sua empresa."
        0 * repository.delete(_)
    }

    void "Deve desvincular competencia da vaga com sucesso quando pertencer a empresa autenticada"() {
        given: "Um vinculo pertencente a empresa autenticada"
        Long vinculoId = 10L
        Long empresaId = 1L

        CompetenciaVaga vinculo = new CompetenciaVaga(id: vinculoId, empresaId: empresaId)
        repository.findById(vinculoId) >> Optional.of(vinculo)

        when: "Executa a desvinculacao"
        useCase.executar(vinculoId, empresaId)

        then: "Chama o delete do repositorio com o vinculo encontrado"
        1 * repository.delete(vinculo)
    }
}
