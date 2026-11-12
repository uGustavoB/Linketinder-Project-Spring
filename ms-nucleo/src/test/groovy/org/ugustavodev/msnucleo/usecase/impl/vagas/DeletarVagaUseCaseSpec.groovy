package org.ugustavodev.msnucleo.usecase.impl.vagas

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.ugustavodev.msnucleo.client.CompetenciaClient
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.CurtidaRepository
import org.ugustavodev.msnucleo.repositories.VagaRepository
import spock.lang.Specification
import spock.lang.Subject

class DeletarVagaUseCaseSpec extends Specification {

    VagaRepository vagaRepository = Mock(VagaRepository)
    CurtidaRepository curtidaRepository = Mock(CurtidaRepository)
    CompetenciaClient competenciaClient = Mock(CompetenciaClient)

    @Subject
    DeletarVagaUseCase useCase = new DeletarVagaUseCase(vagaRepository, curtidaRepository, competenciaClient)

    void cleanup() {
        SecurityContextHolder.clearContext()
    }

    void "Deve lancar excecao quando o ID da vaga for nulo"() {
        given: "Um ID de vaga nulo e um ID de empresa valido"
        Long vagaId = null
        Long empresaId = 1L

        when: "Tenta executar com vagaId nulo"
        useCase.executar(vagaId, empresaId)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID da vaga não pode ser nulo."

        and: "Nenhum metodo de remocao deve ser chamado"
        0 * competenciaClient.excluirCompetenciasVaga(*_)
        0 * curtidaRepository.deleteByVagaId(_)
        0 * vagaRepository.deleteById(_)
    }

    void "Deve lancar excecao quando a vaga nao for encontrada"() {
        given: "Um ID de vaga inexistente"
        Long vagaId = 99L
        Long empresaId = 1L

        and: "O repositorio retorna que a vaga nao existe"
        vagaRepository.findById(vagaId) >> Optional.empty()

        when: "Tenta deletar a vaga inexistente"
        useCase.executar(vagaId, empresaId)

        then: "Lanca NoSuchElementException"
        NoSuchElementException ex = thrown(NoSuchElementException)
        ex.message == "Vaga não encontrada para o ID: ${vagaId}"

        and: "Nenhum metodo de remocao deve ser chamado"
        0 * competenciaClient.excluirCompetenciasVaga(*_)
        0 * curtidaRepository.deleteByVagaId(_)
        0 * vagaRepository.deleteById(_)
    }

    void "Deve lancar excecao quando a vaga pertencer a outra empresa"() {
        given: "Uma vaga pertencente a empresa 1"
        Long vagaId = 10L
        Long empresaDonaId = 1L
        Long outraEmpresaId = 2L

        Empresa empresaDona = new Empresa(id: empresaDonaId)
        Vaga vagaExistente = new Vaga(id: vagaId, empresa: empresaDona)

        and: "O repositorio encontra a vaga"
        vagaRepository.findById(vagaId) >> Optional.of(vagaExistente)

        when: "Uma empresa diferente tenta deletar a vaga"
        useCase.executar(vagaId, outraEmpresaId)

        then: "Lanca AccessDeniedException de violacao de permissao"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Você não tem permissão para deletar esta vaga."

        and: "Nenhum metodo de remocao deve ser chamado"
        0 * competenciaClient.excluirCompetenciasVaga(*_)
        0 * curtidaRepository.deleteByVagaId(_)
        0 * vagaRepository.deleteById(_)
    }

    void "Deve deletar as curtidas da vaga e em seguida a vaga com sucesso"() {
        given: "Uma vaga pertencente a empresa logada autenticada"
        Long vagaId = 10L
        Long empresaId = 1L

        Usuario usuario = new Usuario(id: 1L, email: "empresa@email.com", role: Role.EMPRESA)
        Empresa empresa = new Empresa(id: empresaId, usuario: usuario)
        Vaga vagaExistente = new Vaga(id: vagaId, empresa: empresa)

        Authentication authentication = Mock(Authentication)
        authentication.getPrincipal() >> usuario
        SecurityContext securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> authentication
        SecurityContextHolder.setContext(securityContext)

        and: "O repositorio encontra a vaga"
        vagaRepository.findById(vagaId) >> Optional.of(vagaExistente)

        when: "Executa a delecao da vaga"
        useCase.executar(vagaId, empresaId)

        then: "Deve deletar as competencias vinculadas a vaga no ms-competencia"
        1 * competenciaClient.excluirCompetenciasVaga(usuario, empresaId, vagaId)

        and: "Primeiro deve deletar as curtidas vinculadas a vaga"
        1 * curtidaRepository.deleteByVagaId(vagaId)

        and: "Em seguida deve deletar a vaga pelo ID"
        1 * vagaRepository.deleteById(vagaId)
    }

}
