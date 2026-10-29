package org.ugustavodev.msnucleo.usecase.impl.candidatos

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.repositories.CurtidaRepository
import spock.lang.Specification
import spock.lang.Subject

class DeletarCandidatoUseCaseSpec extends Specification {

    CandidatoRepository candidatoRepository = Mock(CandidatoRepository)
    CurtidaRepository curtidaRepository = Mock(CurtidaRepository)

    @Subject
    DeletarCandidatoUseCase useCase = new DeletarCandidatoUseCase(candidatoRepository, curtidaRepository)

    void cleanup() {
        SecurityContextHolder.clearContext()
    }

    void "Deve lancar excecao quando o ID do candidato for nulo"() {
        given: "Um ID nulo"
        Long id = null

        when: "Tenta deletar com ID nulo"
        useCase.executar(id)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID do candidato não pode ser nulo."
    }

    void "Deve lancar excecao quando o candidato nao for encontrado"() {
        given: "Um ID inexistente"
        Long id = 99L
        1 * candidatoRepository.findById(id) >> Optional.empty()

        when: "Tenta deletar candidato inexistente"
        useCase.executar(id)

        then: "Lanca NoSuchElementException"
        NoSuchElementException ex = thrown(NoSuchElementException)
        ex.message == "Candidato não encontrado."
    }

    void "Deve bloquear a exclusao quando outro candidato tentar deletar conta alheia"() {
        given: "Um candidato existente e outro candidato diferente autenticado"
        Long id = 1L
        Candidato candidato = new Candidato(id: id, usuario: new Usuario(email: "joao@email.com", role: Role.CANDIDATO))
        Usuario outroCandidato = new Usuario(id: 2L, email: "outro@email.com", role: Role.CANDIDATO)

        Authentication authentication = Mock(Authentication)
        authentication.getPrincipal() >> outroCandidato
        SecurityContext securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> authentication
        SecurityContextHolder.setContext(securityContext)

        1 * candidatoRepository.findById(id) >> Optional.of(candidato)

        when: "Outro candidato tenta deletar a conta"
        useCase.executar(id)

        then: "Lanca AccessDeniedException e nao remove registros"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Você não tem permissão para deletar a conta de outro candidato."
        0 * curtidaRepository.deleteByCandidatoId(_)
        0 * candidatoRepository.delete(_)
    }

    void "Deve remover as curtidas e deletar o candidato com sucesso"() {
        given: "O candidato titular autenticado"
        Long id = 1L
        Usuario usuarioTitular = new Usuario(id: 1L, email: "joao@email.com", role: Role.CANDIDATO)
        Candidato candidato = new Candidato(id: id, usuario: usuarioTitular)

        Authentication authentication = Mock(Authentication)
        authentication.getPrincipal() >> usuarioTitular
        SecurityContext securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> authentication
        SecurityContextHolder.setContext(securityContext)

        1 * candidatoRepository.findById(id) >> Optional.of(candidato)

        when: "O titular deleta sua conta"
        useCase.executar(id)

        then: "Deleta primeiro as curtidas e em seguida o candidato"
        1 * curtidaRepository.deleteByCandidatoId(id)
        1 * candidatoRepository.delete(candidato)
    }

}
