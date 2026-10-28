package org.ugustavodev.msnucleo.usecase.impl.vagas

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.dto.response.VagaResponseDTO
import org.ugustavodev.msnucleo.mapper.VagaMapper
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.repositories.CurtidaRepository
import org.ugustavodev.msnucleo.repositories.VagaRepository
import spock.lang.Specification
import spock.lang.Subject

class BuscarVagaPorIdUseCaseSpec extends Specification {

    VagaRepository vagaRepository = Mock(VagaRepository)
    VagaMapper vagaMapper = Mock(VagaMapper)
    CurtidaRepository curtidaRepository = Mock(CurtidaRepository)
    CandidatoRepository candidatoRepository = Mock(CandidatoRepository)

    @Subject
    BuscarVagaPorIdUseCase useCase = new BuscarVagaPorIdUseCase(
            vagaRepository,
            vagaMapper,
            curtidaRepository,
            candidatoRepository
    )

    void cleanup() {
        SecurityContextHolder.clearContext()
    }

    void "Deve lancar excecao quando o ID da vaga for nulo"() {
        given: "Um ID nulo"
        Long id = null

        when: "Executa a busca com ID nulo"
        useCase.executar(id)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID da vaga não pode ser nulo."
    }

    void "Deve lancar excecao quando a vaga nao for encontrada"() {
        given: "Um ID de vaga inexistente"
        Long id = 99L
        1 * vagaRepository.findById(id) >> Optional.empty()

        when: "Executa a busca"
        useCase.executar(id)

        then: "Lanca NoSuchElementException"
        NoSuchElementException ex = thrown(NoSuchElementException)
        ex.message == "Vaga não encontrada para o ID: ${id}"
    }

    void "Deve ocultar empresaId e empresaNome quando consultada por candidato SEM match na vaga"() {
        given: "Uma vaga existente e um candidato autenticado sem match"
        Long vagaId = 5L
        Long candidatoId = 10L
        Empresa empresa = new Empresa(id: 1L, nome: "Tech Solutions Ltda", usuario: new Usuario(email: "rh@tech.com"))
        Vaga vaga = new Vaga(
                id: vagaId,
                nome: "Dev Groovy",
                descricao: "Spring Boot",
                local: "Remoto",
                nivel: "Pleno",
                empresa: empresa
        )
        Usuario usuarioCandidato = new Usuario(id: 2L, email: "joao@email.com", role: Role.CANDIDATO)
        Candidato candidato = new Candidato(id: candidatoId, usuario: usuarioCandidato)

        Authentication authentication = Mock(Authentication)
        authentication.getPrincipal() >> usuarioCandidato
        SecurityContext securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> authentication
        SecurityContextHolder.setContext(securityContext)

        1 * vagaRepository.findById(vagaId) >> Optional.of(vaga)
        1 * candidatoRepository.findByUsuarioEmail("joao@email.com") >> Optional.of(candidato)
        1 * curtidaRepository.existsMatchNaVaga(candidatoId, vagaId) >> false

        when: "O candidato sem match busca a vaga por ID"
        VagaResponseDTO resultado = useCase.executar(vagaId)

        then: "Retorna os dados da vaga mantendo empresaId e empresaNome nulos"
        resultado.id() == vagaId
        resultado.nome() == "Dev Groovy"
        resultado.empresaId() == null
        resultado.empresaNome() == null
    }

    void "Deve revelar empresaId e empresaNome quando consultada por candidato COM match na vaga"() {
        given: "Uma vaga existente e um candidato autenticado que possui match confirmado"
        Long vagaId = 5L
        Long candidatoId = 10L
        Empresa empresa = new Empresa(id: 1L, nome: "Tech Solutions Ltda", usuario: new Usuario(email: "rh@tech.com"))
        Vaga vaga = new Vaga(id: vagaId, nome: "Dev Groovy", empresa: empresa)
        Usuario usuarioCandidato = new Usuario(id: 2L, email: "joao@email.com", role: Role.CANDIDATO)
        Candidato candidato = new Candidato(id: candidatoId, usuario: usuarioCandidato)
        VagaResponseDTO dtoCompleto = new VagaResponseDTO(vagaId, "Dev Groovy", "Spring Boot", "Remoto", "Pleno", 1L, "Tech Solutions Ltda")

        Authentication authentication = Mock(Authentication)
        authentication.getPrincipal() >> usuarioCandidato
        SecurityContext securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> authentication
        SecurityContextHolder.setContext(securityContext)

        1 * vagaRepository.findById(vagaId) >> Optional.of(vaga)
        1 * candidatoRepository.findByUsuarioEmail("joao@email.com") >> Optional.of(candidato)
        1 * curtidaRepository.existsMatchNaVaga(candidatoId, vagaId) >> true
        1 * vagaMapper.toResponseDTO(vaga) >> dtoCompleto

        when: "O candidato com match busca a vaga por ID"
        VagaResponseDTO resultado = useCase.executar(vagaId)

        then: "Retorna o DTO completo com empresaId e empresaNome"
        resultado == dtoCompleto
        resultado.empresaId() == 1L
        resultado.empresaNome() == "Tech Solutions Ltda"
    }

    void "Deve revelar empresaId e empresaNome quando consultada pela propria empresa dona da vaga"() {
        given: "A empresa autora da vaga autenticada"
        Long vagaId = 5L
        Usuario usuarioEmpresa = new Usuario(id: 1L, email: "rh@tech.com", role: Role.EMPRESA)
        Empresa empresa = new Empresa(id: 1L, nome: "Tech Solutions Ltda", usuario: usuarioEmpresa)
        Vaga vaga = new Vaga(id: vagaId, nome: "Dev Groovy", empresa: empresa)
        VagaResponseDTO dtoCompleto = new VagaResponseDTO(vagaId, "Dev Groovy", "Spring Boot", "Remoto", "Pleno", 1L, "Tech Solutions Ltda")

        Authentication authentication = Mock(Authentication)
        authentication.getPrincipal() >> usuarioEmpresa
        SecurityContext securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> authentication
        SecurityContextHolder.setContext(securityContext)

        1 * vagaRepository.findById(vagaId) >> Optional.of(vaga)
        1 * vagaMapper.toResponseDTO(vaga) >> dtoCompleto

        when: "A empresa dona consulta a vaga"
        VagaResponseDTO resultado = useCase.executar(vagaId)

        then: "Retorna o DTO completo"
        resultado == dtoCompleto
    }
}
