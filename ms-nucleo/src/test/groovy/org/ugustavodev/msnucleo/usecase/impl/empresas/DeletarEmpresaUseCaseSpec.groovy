package org.ugustavodev.msnucleo.usecase.impl.empresas

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.repositories.CurtidaRepository
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.repositories.VagaRepository
import spock.lang.Specification
import spock.lang.Subject

class DeletarEmpresaUseCaseSpec extends Specification {

    EmpresaRepository empresaRepository = Mock(EmpresaRepository)
    CurtidaRepository curtidaRepository = Mock(CurtidaRepository)
    VagaRepository vagaRepository = Mock(VagaRepository)

    @Subject
    DeletarEmpresaUseCase useCase = new DeletarEmpresaUseCase(empresaRepository, curtidaRepository, vagaRepository)

    void cleanup() {
        SecurityContextHolder.clearContext()
    }

    void "Deve lancar excecao quando o ID da empresa for nulo"() {
        given: "Um ID nulo"
        Long id = null

        when: "Tenta deletar com ID nulo"
        useCase.executar(id)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID da empresa não pode ser nulo."
    }

    void "Deve lancar excecao quando a empresa nao for encontrada"() {
        given: "Um ID inexistente"
        Long id = 99L
        1 * empresaRepository.findById(id) >> Optional.empty()

        when: "Tenta deletar empresa inexistente"
        useCase.executar(id)

        then: "Lanca NoSuchElementException"
        NoSuchElementException ex = thrown(NoSuchElementException)
        ex.message == "Empresa não encontrada."
    }

    void "Deve bloquear a exclusao quando outra empresa tentar deletar conta alheia"() {
        given: "Uma empresa existente e outra empresa autenticada"
        Long id = 1L
        Empresa empresa = new Empresa(id: id, usuario: new Usuario(email: "rh@empresa1.com", role: Role.EMPRESA))
        Usuario outraEmpresa = new Usuario(id: 2L, email: "rh@empresa2.com", role: Role.EMPRESA)

        Authentication authentication = Mock(Authentication)
        authentication.getPrincipal() >> outraEmpresa
        SecurityContext securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> authentication
        SecurityContextHolder.setContext(securityContext)

        1 * empresaRepository.findById(id) >> Optional.of(empresa)

        when: "A outra empresa tenta deletar a conta"
        useCase.executar(id)

        then: "Lanca AccessDeniedException e nao remove registros"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Você não tem permissão para deletar a conta de outra empresa."
        0 * curtidaRepository.deleteByVagaEmpresaId(_)
        0 * vagaRepository.deleteByEmpresaId(_)
        0 * empresaRepository.delete(_)
    }

    void "Deve deletar em cascata as curtidas, as vagas e a empresa com sucesso"() {
        given: "A empresa titular autenticada"
        Long id = 1L
        Usuario usuarioTitular = new Usuario(id: 1L, email: "rh@techsolutions.com.br", role: Role.EMPRESA)
        Empresa empresa = new Empresa(id: id, usuario: usuarioTitular)

        Authentication authentication = Mock(Authentication)
        authentication.getPrincipal() >> usuarioTitular
        SecurityContext securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> authentication
        SecurityContextHolder.setContext(securityContext)

        1 * empresaRepository.findById(id) >> Optional.of(empresa)

        when: "Executa a exclusao da empresa"
        useCase.executar(id)

        then: "Deleta curtidas das vagas, vagas da empresa e por fim a empresa"
        1 * curtidaRepository.deleteByVagaEmpresaId(id)
        1 * vagaRepository.deleteByEmpresaId(id)
        1 * empresaRepository.delete(empresa)
    }

}
