package org.ugustavodev.msnucleo.usecase.impl.candidatos

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.dto.request.CandidatoRequestDTO
import org.ugustavodev.msnucleo.dto.response.CandidatoResponseDTO
import org.ugustavodev.msnucleo.mapper.CandidatoMapper
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

class AtualizarCandidatoUseCaseSpec extends Specification {

    CandidatoRepository candidatoRepository = Mock(CandidatoRepository)
    PasswordEncoder passwordEncoder = Mock(PasswordEncoder)
    CandidatoMapper candidatoMapper = Mock(CandidatoMapper)

    @Subject
    AtualizarCandidatoUseCase useCase = new AtualizarCandidatoUseCase(candidatoRepository, passwordEncoder, candidatoMapper)

    void cleanup() {
        SecurityContextHolder.clearContext()
    }

    void "Deve lancar excecao quando o ID do candidato for nulo"() {
        given: "Um ID nulo e um request valido"
        Long id = null
        CandidatoRequestDTO request = new CandidatoRequestDTO()

        when: "Tenta atualizar com ID nulo"
        useCase.executar(id, request)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID do candidato não pode ser nulo."
    }

    void "Deve lancar excecao quando o requestDTO for nulo"() {
        given: "Um ID valido e um request nulo"
        Long id = 1L
        CandidatoRequestDTO request = null

        when: "Tenta atualizar com request nulo"
        useCase.executar(id, request)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O objeto CandidatoRequestDTO não pode ser nulo."
    }

    void "Deve lancar excecao quando o candidato nao for encontrado"() {
        given: "Um ID de candidato inexistente"
        Long id = 99L
        CandidatoRequestDTO request = new CandidatoRequestDTO()
        1 * candidatoRepository.findById(id) >> Optional.empty()

        when: "Tenta atualizar candidato inexistente"
        useCase.executar(id, request)

        then: "Lanca NoSuchElementException"
        NoSuchElementException ex = thrown(NoSuchElementException)
        ex.message == "Candidato não encontrado."
    }

    void "Deve bloquear a atualizacao quando o usuario logado for uma EMPRESA"() {
        given: "Um candidato existente e uma empresa autenticada"
        Long id = 1L
        Candidato candidato = new Candidato(id: id, usuario: new Usuario(email: "joao@email.com", role: Role.CANDIDATO))
        CandidatoRequestDTO request = new CandidatoRequestDTO(email: "joao@email.com", cpf: "12345678901")
        Usuario empresaLogada = new Usuario(id: 2L, email: "rh@empresa.com", role: Role.EMPRESA)

        Authentication authentication = Mock(Authentication)
        authentication.getPrincipal() >> empresaLogada
        SecurityContext securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> authentication
        SecurityContextHolder.setContext(securityContext)

        1 * candidatoRepository.findById(id) >> Optional.of(candidato)

        when: "A empresa tenta atualizar o candidato"
        useCase.executar(id, request)

        then: "Lanca AccessDeniedException"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Empresas não têm permissão para atualizar o perfil de candidatos."
    }

    void "Deve lancar excecao quando o novo email ja pertencer a outro candidato"() {
        given: "Um candidato tentando alterar o email para um ja utilizado por outro ID"
        Long id = 1L
        Candidato candidatoExistente = new Candidato(
                id: id,
                cpf: "12345678901",
                usuario: new Usuario(email: "joao@email.com", role: Role.CANDIDATO)
        )
        Candidato outroCandidato = new Candidato(id: 2L, usuario: new Usuario(email: "emuso@email.com"))
        CandidatoRequestDTO request = new CandidatoRequestDTO(email: "emuso@email.com", cpf: "12345678901")

        1 * candidatoRepository.findById(id) >> Optional.of(candidatoExistente)
        1 * candidatoRepository.findByUsuarioEmail("emuso@email.com") >> Optional.of(outroCandidato)

        when: "Executa a atualizacao"
        useCase.executar(id, request)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "Já existe outro candidato cadastrado com o email informado"
    }

    void "Deve atualizar os dados e a senha do candidato com sucesso"() {
        given: "Um candidato titular autenticado enviando novos dados válidos"
        Long id = 1L
        LocalDate dataNascimento = LocalDate.of(1998, 5, 15)
        Usuario usuarioTitular = new Usuario(id: 1L, email: "joao@email.com", senha: "old_hash", role: Role.CANDIDATO)
        Candidato candidatoExistente = new Candidato(
                id: id,
                nome: "João",
                sobrenome: "Silva",
                cpf: "12345678901",
                usuario: usuarioTitular
        )
        CandidatoRequestDTO request = new CandidatoRequestDTO(
                nome: "João Atualizado",
                sobrenome: "Silva",
                dataNascimento: dataNascimento,
                email: "joao@email.com",
                cpf: "12345678901",
                pais: "Brasil",
                estado: "SP",
                cep: "01001000",
                descricao: "Dev Sênior",
                senha: "nova_senha_valida"
        )
        CandidatoResponseDTO esperado = new CandidatoResponseDTO(
                id, "João Atualizado", "Silva", dataNascimento,
                "joao@email.com", "12345678901", "Brasil", "SP", "01001000", "Dev Sênior", []
        )

        Authentication authentication = Mock(Authentication)
        authentication.getPrincipal() >> usuarioTitular
        SecurityContext securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> authentication
        SecurityContextHolder.setContext(securityContext)

        1 * candidatoRepository.findById(id) >> Optional.of(candidatoExistente)
        1 * passwordEncoder.encode("nova_senha_valida") >> "novo_hash"
        1 * candidatoRepository.save(candidatoExistente) >> candidatoExistente
        1 * candidatoMapper.toResponseDTO(candidatoExistente) >> esperado

        when: "Executa a atualizacao"
        CandidatoResponseDTO resultado = useCase.executar(id, request)

        then: "Os dados e a senha criptografada sao atualizados e retorna o DTO"
        candidatoExistente.nome == "João Atualizado"
        candidatoExistente.usuario.senha == "novo_hash"
        resultado == esperado
    }

}
