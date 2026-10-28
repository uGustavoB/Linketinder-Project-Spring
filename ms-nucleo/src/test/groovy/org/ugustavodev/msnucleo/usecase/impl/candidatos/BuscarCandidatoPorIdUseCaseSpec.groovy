package org.ugustavodev.msnucleo.usecase.impl.candidatos

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.dto.response.CandidatoResponseDTO
import org.ugustavodev.msnucleo.mapper.CandidatoMapper
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

class BuscarCandidatoPorIdUseCaseSpec extends Specification {

    CandidatoRepository candidatoRepository = Mock(CandidatoRepository)
    CandidatoMapper candidatoMapper = Mock(CandidatoMapper)

    @Subject
    BuscarCandidatoPorIdUseCase useCase = new BuscarCandidatoPorIdUseCase(candidatoRepository, candidatoMapper)

    void cleanup() {
        SecurityContextHolder.clearContext()
    }

    void "Deve lancar excecao quando o ID do candidato for nulo"() {
        given: "Um ID de candidato nulo"
        Long id = null

        when: "Executa a busca com ID nulo"
        useCase.executar(id)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID do candidato não pode ser nulo."
    }

    void "Deve lancar excecao quando o candidato nao for encontrado"() {
        given: "Um ID de candidato inexistente"
        Long id = 99L
        1 * candidatoRepository.findById(id) >> Optional.empty()

        when: "Executa a busca pelo ID"
        useCase.executar(id)

        then: "Lanca NoSuchElementException"
        NoSuchElementException ex = thrown(NoSuchElementException)
        ex.message == "Candidato não encontrado."
    }

    void "Deve bloquear o acesso quando o requisitante for uma EMPRESA (Principio do Anonimato)"() {
        given: "Um candidato existente e uma empresa autenticada no contexto de seguranca"
        Long id = 1L
        Candidato candidato = new Candidato(id: id, usuario: new Usuario(email: "joao@email.com", role: Role.CANDIDATO))
        Usuario empresaLogada = new Usuario(id: 2L, email: "rh@empresa.com", role: Role.EMPRESA)

        Authentication authentication = Mock(Authentication)
        authentication.getPrincipal() >> empresaLogada
        SecurityContext securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> authentication
        SecurityContextHolder.setContext(securityContext)

        1 * candidatoRepository.findById(id) >> Optional.of(candidato)

        when: "A empresa tenta buscar os dados completos do candidato por ID"
        useCase.executar(id)

        then: "Lanca AccessDeniedException"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Empresas não têm permissão para visualizar os dados sensíveis dos candidatos antes do Match."
    }

    void "Deve bloquear o acesso quando outro CANDIDATO tentar visualizar perfil de terceiro"() {
        given: "Um candidato existente e outro candidato diferente autenticado"
        Long id = 1L
        Candidato candidato = new Candidato(id: id, usuario: new Usuario(email: "joao@email.com", role: Role.CANDIDATO))
        Usuario outroCandidatoLogado = new Usuario(id: 3L, email: "outro@email.com", role: Role.CANDIDATO)

        Authentication authentication = Mock(Authentication)
        authentication.getPrincipal() >> outroCandidatoLogado
        SecurityContext securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> authentication
        SecurityContextHolder.setContext(securityContext)

        1 * candidatoRepository.findById(id) >> Optional.of(candidato)

        when: "Outro candidato tenta buscar o perfil pelo ID"
        useCase.executar(id)

        then: "Lanca AccessDeniedException"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Você não tem permissão para visualizar o perfil de outro candidato."
    }

    void "Deve retornar os dados completos do candidato quando consultado pelo proprio titular"() {
        given: "O candidato titular autenticado no contexto de seguranca"
        Long id = 1L
        Usuario usuarioTitular = new Usuario(id: 1L, email: "joao@email.com", role: Role.CANDIDATO)
        Candidato candidato = new Candidato(id: id, nome: "João", sobrenome: "Silva", usuario: usuarioTitular)
        CandidatoResponseDTO esperado = new CandidatoResponseDTO(
                id, "João", "Silva", LocalDate.of(1998, 5, 15),
                "joao@email.com", "12345678901", "Brasil", "SP", "01001000", "Dev Backend", []
        )

        Authentication authentication = Mock(Authentication)
        authentication.getPrincipal() >> usuarioTitular
        SecurityContext securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> authentication
        SecurityContextHolder.setContext(securityContext)

        1 * candidatoRepository.findById(id) >> Optional.of(candidato)
        1 * candidatoMapper.toResponseDTO(candidato) >> esperado

        when: "O titular busca seu proprio perfil por ID"
        CandidatoResponseDTO resultado = useCase.executar(id)

        then: "Retorna o CandidatoResponseDTO completo"
        resultado == esperado
    }

}
