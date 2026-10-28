package org.ugustavodev.msnucleo.usecase.impl.candidatos

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.dto.response.CandidatoPublicoResponseDTO
import org.ugustavodev.msnucleo.mapper.CandidatoMapper
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import spock.lang.Specification
import spock.lang.Subject

class ListarCandidatosUseCaseSpec extends Specification {

    CandidatoRepository candidatoRepository = Mock(CandidatoRepository)
    CandidatoMapper candidatoMapper = Mock(CandidatoMapper)

    @Subject
    ListarCandidatosUseCase useCase = new ListarCandidatosUseCase(candidatoRepository, candidatoMapper)

    void cleanup() {
        SecurityContextHolder.clearContext()
    }

    void "Deve listar todos os candidatos de forma anonimizada quando requisitado por uma EMPRESA"() {
        given: "Uma empresa autenticada e dois candidatos cadastrados"
        Usuario empresaLogada = new Usuario(id: 2L, email: "rh@empresa.com", role: Role.EMPRESA)
        Authentication authentication = Mock(Authentication)
        authentication.getPrincipal() >> empresaLogada
        SecurityContext securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> authentication
        SecurityContextHolder.setContext(securityContext)

        Candidato c1 = new Candidato(id: 1L, pais: "Brasil", estado: "SP", descricao: "Dev Java")
        Candidato c2 = new Candidato(id: 2L, pais: "Brasil", estado: "RJ", descricao: "Dev Angular")
        CandidatoPublicoResponseDTO dto1 = new CandidatoPublicoResponseDTO(1L, "Brasil", "SP", "Dev Java", [])
        CandidatoPublicoResponseDTO dto2 = new CandidatoPublicoResponseDTO(2L, "Brasil", "RJ", "Dev Angular", [])

        1 * candidatoRepository.findAll() >> [c1, c2]
        1 * candidatoMapper.toPublicoResponseDTO(c1) >> dto1
        1 * candidatoMapper.toPublicoResponseDTO(c2) >> dto2

        when: "Executa a listagem de candidatos"
        List<CandidatoPublicoResponseDTO> resultado = useCase.executar()

        then: "Retorna a lista contendo todos os candidatos anonimizados"
        resultado.size() == 2
        resultado == [dto1, dto2]
    }

    void "Deve retornar apenas o proprio perfil publico quando requisitado por um CANDIDATO autenticado"() {
        given: "Um candidato autenticado"
        Usuario candidatoLogado = new Usuario(id: 1L, email: "joao@email.com", role: Role.CANDIDATO)
        Authentication authentication = Mock(Authentication)
        authentication.getPrincipal() >> candidatoLogado
        SecurityContext securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> authentication
        SecurityContextHolder.setContext(securityContext)

        Candidato candidato = new Candidato(id: 1L, pais: "Brasil", estado: "SP", descricao: "Dev Java", usuario: candidatoLogado)
        CandidatoPublicoResponseDTO dto = new CandidatoPublicoResponseDTO(1L, "Brasil", "SP", "Dev Java", [])

        1 * candidatoRepository.findByUsuarioEmail("joao@email.com") >> Optional.of(candidato)
        1 * candidatoMapper.toPublicoResponseDTO(candidato) >> dto
        0 * candidatoRepository.findAll()

        when: "Executa a listagem de candidatos"
        List<CandidatoPublicoResponseDTO> resultado = useCase.executar()

        then: "Retorna apenas o proprio candidato"
        resultado.size() == 1
        resultado[0] == dto
    }
}
