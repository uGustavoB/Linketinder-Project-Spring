package org.ugustavodev.msnucleo.usecase.impl.empresas

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.dto.request.EmpresaRequestDTO
import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO
import org.ugustavodev.msnucleo.mapper.EmpresaMapper
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import spock.lang.Specification
import spock.lang.Subject

class AtualizarEmpresaUseCaseSpec extends Specification {

    EmpresaRepository empresaRepository = Mock(EmpresaRepository)
    PasswordEncoder passwordEncoder = Mock(PasswordEncoder)
    EmpresaMapper empresaMapper = Mock(EmpresaMapper)

    @Subject
    AtualizarEmpresaUseCase useCase = new AtualizarEmpresaUseCase(empresaRepository, passwordEncoder, empresaMapper)

    void cleanup() {
        SecurityContextHolder.clearContext()
    }

    void "Deve lancar excecao quando o ID da empresa for nulo"() {
        given: "Um ID nulo e um request valido"
        Long id = null
        EmpresaRequestDTO request = new EmpresaRequestDTO()

        when: "Tenta atualizar com ID nulo"
        useCase.executar(id, request)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID da empresa não pode ser nulo."
    }

    void "Deve lancar excecao quando o requestDTO for nulo"() {
        given: "Um ID valido e um request nulo"
        Long id = 1L
        EmpresaRequestDTO request = null

        when: "Tenta atualizar com request nulo"
        useCase.executar(id, request)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O objeto EmpresaRequestDTO não pode ser nulo."
    }

    void "Deve bloquear a atualizacao quando outra empresa tentar alterar perfil alheio"() {
        given: "Uma empresa existente e outra empresa autenticada"
        Long id = 1L
        Empresa empresaExistente = new Empresa(id: id, usuario: new Usuario(email: "rh@empresa1.com", role: Role.EMPRESA))
        Usuario outraEmpresa = new Usuario(id: 2L, email: "rh@empresa2.com", role: Role.EMPRESA)
        EmpresaRequestDTO request = new EmpresaRequestDTO(email: "rh@empresa1.com", cnpj: "12345678000199")

        Authentication authentication = Mock(Authentication)
        authentication.getPrincipal() >> outraEmpresa
        SecurityContext securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> authentication
        SecurityContextHolder.setContext(securityContext)

        1 * empresaRepository.findById(id) >> Optional.of(empresaExistente)

        when: "A outra empresa tenta atualizar os dados"
        useCase.executar(id, request)

        then: "Lanca AccessDeniedException"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Você não tem permissão para modificar o perfil de outra empresa."
    }

    void "Deve lancar excecao quando o novo CNPJ ja pertencer a outra empresa"() {
        given: "Uma empresa tentando alterar para o CNPJ de outra empresa"
        Long id = 1L
        Empresa empresaExistente = new Empresa(
                id: id,
                cnpj: "12345678000199",
                usuario: new Usuario(email: "rh@empresa1.com", role: Role.EMPRESA)
        )
        Empresa outraEmpresa = new Empresa(id: 2L, cnpj: "99887766000100")
        EmpresaRequestDTO request = new EmpresaRequestDTO(email: "rh@empresa1.com", cnpj: "99887766000100")

        1 * empresaRepository.findById(id) >> Optional.of(empresaExistente)
        1 * empresaRepository.findByCnpj("99887766000100") >> Optional.of(outraEmpresa)

        when: "Executa a atualizacao"
        useCase.executar(id, request)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "Já existe outra empresa cadastrada com o CNPJ informado"
    }

    void "Deve atualizar os dados cadastrais da empresa com sucesso"() {
        given: "A empresa titular autenticada enviando dados atualizados"
        Long id = 1L
        Usuario usuarioTitular = new Usuario(id: 1L, email: "rh@techsolutions.com.br", senha: "old_hash", role: Role.EMPRESA)
        Empresa empresaExistente = new Empresa(
                id: id,
                nome: "Tech Solutions Ltda",
                cnpj: "12345678000199",
                usuario: usuarioTitular
        )
        EmpresaRequestDTO request = new EmpresaRequestDTO(
                nome: "Tech Solutions Brasil Ltda",
                cnpj: "12345678000199",
                email: "rh@techsolutions.com.br",
                pais: "Brasil",
                estado: "SP",
                cep: "04538132",
                descricao: "Consultoria Global"
        )
        EmpresaResponseDTO esperado = new EmpresaResponseDTO(
                id, "Tech Solutions Brasil Ltda", "12345678000199", "rh@techsolutions.com.br",
                "Brasil", "SP", "04538132", "Consultoria Global"
        )

        Authentication authentication = Mock(Authentication)
        authentication.getPrincipal() >> usuarioTitular
        SecurityContext securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> authentication
        SecurityContextHolder.setContext(securityContext)

        1 * empresaRepository.findById(id) >> Optional.of(empresaExistente)
        1 * empresaRepository.save(empresaExistente) >> empresaExistente
        1 * empresaMapper.toResponseDTO(empresaExistente) >> esperado

        when: "Executa a atualizacao"
        EmpresaResponseDTO resultado = useCase.executar(id, request)

        then: "Atualiza o nome e descricao mantendo a senha anterior"
        empresaExistente.nome == "Tech Solutions Brasil Ltda"
        empresaExistente.usuario.senha == "old_hash"
        resultado == esperado
    }
}
