package org.ugustavodev.msnucleo.usecase.impl.vagas

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.dto.response.VagaPublicoResponseDTO
import org.ugustavodev.msnucleo.mapper.VagaMapper
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.repositories.VagaRepository
import spock.lang.Specification
import spock.lang.Subject

class ListarVagasUseCaseSpec extends Specification {

    VagaRepository vagaRepository = Mock(VagaRepository)
    VagaMapper vagaMapper = Mock(VagaMapper)
    EmpresaRepository empresaRepository = Mock(EmpresaRepository)

    @Subject
    ListarVagasUseCase useCase = new ListarVagasUseCase(vagaRepository, vagaMapper, empresaRepository)

    void cleanup() {
        SecurityContextHolder.clearContext()
    }

    void "Deve listar apenas as vagas da empresa quando empresaId for informado por ela mesma"() {
        given: "Um empresaId valido, usuario autenticado como EMPRESA titular e uma vaga pertencente a ela"
        Long empresaId = 1L
        Usuario usuarioEmpresa = new Usuario(id: 1L, email: "empresa@teste.com", role: Role.EMPRESA)
        Empresa empresa = new Empresa(id: empresaId, usuario: usuarioEmpresa)
        Vaga vaga = new Vaga(id: 5L, nome: "Dev Groovy", empresa: empresa)
        VagaPublicoResponseDTO dto = new VagaPublicoResponseDTO(5L, "Dev Groovy", "Desc", "Remoto", "Pleno", empresaId)

        Authentication auth = Mock(Authentication)
        auth.getPrincipal() >> usuarioEmpresa
        SecurityContextHolder.getContext().setAuthentication(auth)

        1 * empresaRepository.findByUsuarioEmail("empresa@teste.com") >> Optional.of(empresa)
        1 * vagaRepository.findByEmpresaId(empresaId) >> [vaga]
        1 * vagaMapper.toPublicoResponseDTO(vaga, true) >> dto
        0 * vagaRepository.findAll()

        when: "Executa a listagem passando empresaId"
        List<VagaPublicoResponseDTO> resultado = useCase.executar(empresaId)

        then: "Retorna apenas as vagas filtradas com empresaId visivel para a dona"
        resultado.size() == 1
        resultado[0] == dto
        resultado[0].empresaId() == empresaId
    }

    void "Deve lancar AccessDeniedException quando candidato tentar filtrar vagas por empresaId"() {
        given: "Um candidato autenticado tentando filtrar por empresaId"
        Long empresaId = 1L
        Usuario usuarioCandidato = new Usuario(id: 10L, email: "candidato@teste.com", role: Role.CANDIDATO)

        Authentication auth = Mock(Authentication)
        auth.getPrincipal() >> usuarioCandidato
        SecurityContextHolder.getContext().setAuthentication(auth)

        when: "Executa a listagem passando empresaId"
        useCase.executar(empresaId)

        then: "Lanca AccessDeniedException para proteger o anonimato"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Candidatos não têm permissão para filtrar vagas por empresa."
        0 * vagaRepository.findByEmpresaId(_)
        0 * vagaRepository.findAll()
    }

    void "Deve lancar AccessDeniedException quando empresa tentar filtrar vagas de outra empresa"() {
        given: "Uma empresa autenticada tentando filtrar vagas de um concorrente"
        Long empresaIdAlheia = 2L
        Usuario usuarioEmpresa = new Usuario(id: 1L, email: "minhaempresa@teste.com", role: Role.EMPRESA)
        Empresa minhaEmpresa = new Empresa(id: 1L, usuario: usuarioEmpresa)

        Authentication auth = Mock(Authentication)
        auth.getPrincipal() >> usuarioEmpresa
        SecurityContextHolder.getContext().setAuthentication(auth)

        1 * empresaRepository.findByUsuarioEmail("minhaempresa@teste.com") >> Optional.of(minhaEmpresa)

        when: "Executa a listagem passando empresaId alheio"
        useCase.executar(empresaIdAlheia)

        then: "Lanca AccessDeniedException"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Você não tem permissão para visualizar as vagas de outra empresa."
        0 * vagaRepository.findByEmpresaId(_)
        0 * vagaRepository.findAll()
    }

    void "Deve listar todas as vagas de forma anonimizada quando empresaId for nulo e solicitante for candidato"() {
        given: "Um candidato autenticado e duas vagas cadastradas"
        Usuario usuarioCandidato = new Usuario(id: 10L, email: "candidato@teste.com", role: Role.CANDIDATO)
        Empresa empresa = new Empresa(id: 1L)
        Vaga v1 = new Vaga(id: 5L, nome: "Dev Groovy", empresa: empresa)
        Vaga v2 = new Vaga(id: 6L, nome: "Dev Angular", empresa: empresa)
        VagaPublicoResponseDTO dto1 = new VagaPublicoResponseDTO(5L, "Dev Groovy", "Desc 1", "Remoto", "Pleno", null)
        VagaPublicoResponseDTO dto2 = new VagaPublicoResponseDTO(6L, "Dev Angular", "Desc 2", "Híbrido", "Júnior", null)

        Authentication auth = Mock(Authentication)
        auth.getPrincipal() >> usuarioCandidato
        SecurityContextHolder.getContext().setAuthentication(auth)

        1 * vagaRepository.findAll() >> [v1, v2]
        1 * vagaMapper.toPublicoResponseDTO(v1, false) >> dto1
        1 * vagaMapper.toPublicoResponseDTO(v2, false) >> dto2

        when: "Executa a listagem sem filtro"
        List<VagaPublicoResponseDTO> resultado = useCase.executar(null)

        then: "Retorna todas as vagas com empresaId nulo para o candidato"
        resultado.size() == 2
        resultado == [dto1, dto2]
        resultado.every { it.empresaId() == null }
    }

}
