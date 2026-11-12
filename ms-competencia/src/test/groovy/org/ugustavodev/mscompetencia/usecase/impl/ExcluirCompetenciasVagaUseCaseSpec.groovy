package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.security.access.AccessDeniedException
import org.ugustavodev.mscompetencia.model.CompetenciaVaga
import org.ugustavodev.mscompetencia.repositories.CompetenciaVagaRepository
import spock.lang.Specification
import spock.lang.Subject

class ExcluirCompetenciasVagaUseCaseSpec extends Specification {

    CompetenciaVagaRepository repository = Mock(CompetenciaVagaRepository)

    @Subject
    ExcluirCompetenciasVagaUseCase useCase = new ExcluirCompetenciasVagaUseCase(repository)

    void "Deve lancar IllegalArgumentException quando vagaId for nulo"() {
        when: "Executa com vagaId nulo"
        useCase.executar(null, 1L)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID da vaga não pode ser nulo."
        0 * repository._
    }

    void "Deve lancar AccessDeniedException quando empresaAutenticadaId for nulo"() {
        when: "Executa com empresaAutenticadaId nulo"
        useCase.executar(1L, null)

        then: "Lanca AccessDeniedException"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Você não tem permissão para remover competências de uma vaga que não pertence à sua empresa."
        0 * repository._
    }

    void "Deve lancar AccessDeniedException quando vaga pertencer a outra empresa (protecao IDOR)"() {
        given: "Uma vaga com vinculos pertencentes a empresa 2"
        Long vagaId = 10L
        Long empresaAutenticadaId = 1L
        Long outraEmpresaId = 2L
        CompetenciaVaga vinculo = new CompetenciaVaga(id: 100L, vagaId: vagaId, empresaId: outraEmpresaId)
        repository.findByVagaId(vagaId) >> [vinculo]

        when: "Empresa 1 tenta excluir competencias da vaga da empresa 2"
        useCase.executar(vagaId, empresaAutenticadaId)

        then: "Lanca AccessDeniedException e nao deleta"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Você não tem permissão para remover competências de uma vaga que não pertence à sua empresa."
        0 * repository.deleteByVagaId(_)
    }

    void "Deve excluir competencias com sucesso quando vaga nao possui vinculos (idempotencia)"() {
        given: "Uma vaga sem vinculos de competencia"
        Long vagaId = 10L
        Long empresaAutenticadaId = 1L
        repository.findByVagaId(vagaId) >> []

        when: "Executa a exclusao das competencias da vaga"
        useCase.executar(vagaId, empresaAutenticadaId)

        then: "Chama o deleteByVagaId do repositorio com sucesso"
        1 * repository.deleteByVagaId(vagaId)
    }

    void "Deve excluir competencias com sucesso quando vaga pertence a empresa autenticada"() {
        given: "Uma vaga com vinculos pertencentes a propria empresa autenticada"
        Long vagaId = 10L
        Long empresaAutenticadaId = 1L
        CompetenciaVaga vinculo = new CompetenciaVaga(id: 100L, vagaId: vagaId, empresaId: empresaAutenticadaId)
        repository.findByVagaId(vagaId) >> [vinculo]

        when: "Executa a exclusao das competencias da vaga"
        useCase.executar(vagaId, empresaAutenticadaId)

        then: "Chama o deleteByVagaId do repositorio exatamente uma vez"
        1 * repository.deleteByVagaId(vagaId)
    }

}
