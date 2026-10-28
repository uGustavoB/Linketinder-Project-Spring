package org.ugustavodev.msnucleo.usecase.impl.empresas

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.dto.response.EmpresaPublicoResponseDTO
import org.ugustavodev.msnucleo.mapper.EmpresaMapper
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import spock.lang.Specification
import spock.lang.Subject

class ListarEmpresasUseCaseSpec extends Specification {

    EmpresaRepository empresaRepository = Mock(EmpresaRepository)
    EmpresaMapper empresaMapper = Mock(EmpresaMapper)

    @Subject
    ListarEmpresasUseCase useCase = new ListarEmpresasUseCase(empresaRepository, empresaMapper)

    void cleanup() {
        SecurityContextHolder.clearContext()
    }

    void "Deve listar todas as empresas de forma anonimizada quando requisitado por um CANDIDATO"() {
        given: "Um candidato autenticado e duas empresas cadastradas"
        Usuario candidatoLogado = new Usuario(id: 1L, email: "joao@email.com", role: Role.CANDIDATO)
        Authentication authentication = Mock(Authentication)
        authentication.getPrincipal() >> candidatoLogado
        SecurityContext securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> authentication
        SecurityContextHolder.setContext(securityContext)

        Empresa e1 = new Empresa(id: 1L, pais: "Brasil", estado: "SP", descricao: "Consultoria TI")
        Empresa e2 = new Empresa(id: 2L, pais: "Brasil", estado: "PR", descricao: "Fintech")
        EmpresaPublicoResponseDTO dto1 = new EmpresaPublicoResponseDTO(1L, "Brasil", "SP", "Consultoria TI")
        EmpresaPublicoResponseDTO dto2 = new EmpresaPublicoResponseDTO(2L, "Brasil", "PR", "Fintech")

        1 * empresaRepository.findAll() >> [e1, e2]
        1 * empresaMapper.toPublicoResponseDTO(e1) >> dto1
        1 * empresaMapper.toPublicoResponseDTO(e2) >> dto2

        when: "Executa a listagem de empresas"
        List<EmpresaPublicoResponseDTO> resultado = useCase.executar()

        then: "Retorna todas as empresas anonimizadas"
        resultado.size() == 2
        resultado == [dto1, dto2]
    }

    void "Deve retornar apenas a propria empresa anonimizada quando requisitado por uma EMPRESA autenticada"() {
        given: "Uma empresa autenticada"
        Usuario empresaLogada = new Usuario(id: 1L, email: "rh@empresa.com", role: Role.EMPRESA)
        Authentication authentication = Mock(Authentication)
        authentication.getPrincipal() >> empresaLogada
        SecurityContext securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> authentication
        SecurityContextHolder.setContext(securityContext)

        Empresa empresa = new Empresa(id: 1L, pais: "Brasil", estado: "SP", descricao: "Consultoria TI", usuario: empresaLogada)
        EmpresaPublicoResponseDTO dto = new EmpresaPublicoResponseDTO(1L, "Brasil", "SP", "Consultoria TI")

        1 * empresaRepository.findByUsuarioEmail("rh@empresa.com") >> Optional.of(empresa)
        1 * empresaMapper.toPublicoResponseDTO(empresa) >> dto
        0 * empresaRepository.findAll()

        when: "Executa a listagem de empresas"
        List<EmpresaPublicoResponseDTO> resultado = useCase.executar()

        then: "Retorna apenas a propria empresa"
        resultado.size() == 1
        resultado[0] == dto
    }

}
