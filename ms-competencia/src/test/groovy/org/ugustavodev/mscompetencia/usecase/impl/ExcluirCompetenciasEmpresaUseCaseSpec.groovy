package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.security.access.AccessDeniedException
import org.ugustavodev.mscompetencia.repositories.CompetenciaEmpresaRepository
import org.ugustavodev.mscompetencia.repositories.CompetenciaVagaRepository
import spock.lang.Specification
import spock.lang.Subject

class ExcluirCompetenciasEmpresaUseCaseSpec extends Specification {

    CompetenciaEmpresaRepository competenciaEmpresaRepository = Mock(CompetenciaEmpresaRepository)
    CompetenciaVagaRepository competenciaVagaRepository = Mock(CompetenciaVagaRepository)

    @Subject
    ExcluirCompetenciasEmpresaUseCase useCase = new ExcluirCompetenciasEmpresaUseCase(
            competenciaEmpresaRepository,
            competenciaVagaRepository
    )

    void "Deve lancar IllegalArgumentException quando empresaId for nulo"() {
        when: "Executa com empresaId nulo"
        useCase.executar(null, 1L)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID da empresa não pode ser nulo."
        0 * competenciaEmpresaRepository._
        0 * competenciaVagaRepository._
    }

    void "Deve lancar AccessDeniedException quando empresaAutenticadaId for nulo"() {
        when: "Executa com empresaAutenticadaId nulo"
        useCase.executar(1L, null)

        then: "Lanca AccessDeniedException"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Você não tem permissão para remover as competências desta empresa."
        0 * competenciaEmpresaRepository._
        0 * competenciaVagaRepository._
    }

    void "Deve lancar AccessDeniedException quando empresaAutenticadaId for diferente de empresaId (protecao IDOR)"() {
        when: "Empresa 2 tenta excluir competencias da empresa 1"
        useCase.executar(1L, 2L)

        then: "Lanca AccessDeniedException"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Você não tem permissão para remover as competências desta empresa."
        0 * competenciaEmpresaRepository._
        0 * competenciaVagaRepository._
    }

    void "Deve excluir atomicamente competencias da empresa e das suas vagas quando for a titular autenticada"() {
        given: "Um ID de empresa autenticada"
        Long empresaId = 1L

        when: "Executa a exclusao com a empresa titular"
        useCase.executar(empresaId, empresaId)

        then: "Remove atomicamente os vinculos da empresa e de suas vagas"
        1 * competenciaEmpresaRepository.deleteByEmpresaId(empresaId)
        1 * competenciaVagaRepository.deleteByEmpresaId(empresaId)
    }

}
