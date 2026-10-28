package org.ugustavodev.msnucleo.usecase.impl.empresas

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO
import org.ugustavodev.msnucleo.mapper.EmpresaMapper
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import spock.lang.Specification
import spock.lang.Subject

class BuscarEmpresaPorIdUseCaseSpec extends Specification {

    EmpresaRepository empresaRepository = Mock(EmpresaRepository)
    EmpresaMapper empresaMapper = Mock(EmpresaMapper)

    @Subject
    BuscarEmpresaPorIdUseCase useCase = new BuscarEmpresaPorIdUseCase(empresaRepository, empresaMapper)

    void cleanup() {
        SecurityContextHolder.clearContext()
    }

    void "Deve lancar excecao quando o ID da empresa for nulo"() {
        given: "Um ID nulo"
        Long id = null

        when: "Executa a busca com ID nulo"
        useCase.executar(id)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID da empresa não pode ser nulo."
    }

    void "Deve lancar excecao quando a empresa nao for encontrada"() {
        given: "Um ID de empresa inexistente"
        Long id = 99L
        1 * empresaRepository.findById(id) >> Optional.empty()

        when: "Executa a busca"
        useCase.executar(id)

        then: "Lanca NoSuchElementException"
        NoSuchElementException ex = thrown(NoSuchElementException)
        ex.message == "Empresa não encontrada."
    }

    void "Deve bloquear o acesso quando um CANDIDATO tentar buscar dados completos da empresa"() {
        given: "Uma empresa existente e um candidato autenticado"
        Long id = 1L
        Empresa empresa = new Empresa(id: id, usuario: new Usuario(email: "rh@empresa.com", role: Role.EMPRESA))
        Usuario candidatoLogado = new Usuario(id: 10L, email: "joao@email.com", role: Role.CANDIDATO)

        Authentication authentication = Mock(Authentication)
        authentication.getPrincipal() >> candidatoLogado
        SecurityContext securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> authentication
        SecurityContextHolder.setContext(securityContext)

        1 * empresaRepository.findById(id) >> Optional.of(empresa)

        when: "O candidato tenta buscar os dados da empresa por ID"
        useCase.executar(id)

        then: "Lanca AccessDeniedException"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Candidatos não têm permissão para visualizar os dados sensíveis das empresas antes do Match."
    }

    void "Deve bloquear o acesso quando outra EMPRESA tentar visualizar perfil de concorrente"() {
        given: "Uma empresa existente e outra empresa diferente autenticada"
        Long id = 1L
        Empresa empresa = new Empresa(id: id, usuario: new Usuario(email: "rh@empresa1.com", role: Role.EMPRESA))
        Usuario outraEmpresaLogada = new Usuario(id: 2L, email: "rh@empresa2.com", role: Role.EMPRESA)

        Authentication authentication = Mock(Authentication)
        authentication.getPrincipal() >> outraEmpresaLogada
        SecurityContext securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> authentication
        SecurityContextHolder.setContext(securityContext)

        1 * empresaRepository.findById(id) >> Optional.of(empresa)

        when: "A outra empresa tenta buscar o perfil completo"
        useCase.executar(id)

        then: "Lanca AccessDeniedException"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Você não tem permissão para visualizar o perfil completo de outra empresa."
    }

    void "Deve retornar os dados completos quando consultado pela propria empresa titular"() {
        given: "A empresa titular autenticada"
        Long id = 1L
        Usuario usuarioTitular = new Usuario(id: 1L, email: "rh@techsolutions.com.br", role: Role.EMPRESA)
        Empresa empresa = new Empresa(id: id, nome: "Tech Solutions Ltda", usuario: usuarioTitular)
        EmpresaResponseDTO esperado = new EmpresaResponseDTO(
                id, "Tech Solutions Ltda", "12345678000199", "rh@techsolutions.com.br",
                "Brasil", "SP", "04538132", "Consultoria TI"
        )

        Authentication authentication = Mock(Authentication)
        authentication.getPrincipal() >> usuarioTitular
        SecurityContext securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> authentication
        SecurityContextHolder.setContext(securityContext)

        1 * empresaRepository.findById(id) >> Optional.of(empresa)
        1 * empresaMapper.toResponseDTO(empresa) >> esperado

        when: "A titular consulta seus dados por ID"
        EmpresaResponseDTO resultado = useCase.executar(id)

        then: "Retorna o EmpresaResponseDTO completo"
        resultado == esperado
    }
}
