package org.ugustavodev.msnucleo.usecase.impl

import org.ugustavodev.msnucleo.client.CompetenciaClient
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.dto.response.CandidatosPorCompetenciaDTO
import org.ugustavodev.msnucleo.dto.response.DashboardCandidatoResponseDTO
import org.ugustavodev.msnucleo.dto.response.DashboardEmpresaResponseDTO
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.repositories.CurtidaRepository
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.repositories.VagaRepository
import spock.lang.Specification
import spock.lang.Subject

class ObterDashboardUseCaseSpec extends Specification {

    CandidatoRepository candidatoRepository = Mock(CandidatoRepository)
    EmpresaRepository empresaRepository = Mock(EmpresaRepository)
    VagaRepository vagaRepository = Mock(VagaRepository)
    CurtidaRepository curtidaRepository = Mock(CurtidaRepository)
    CompetenciaClient competenciaClient = Mock(CompetenciaClient)

    @Subject
    ObterDashboardUseCase useCase = new ObterDashboardUseCase(
            candidatoRepository,
            empresaRepository,
            vagaRepository,
            curtidaRepository,
            competenciaClient
    )

    void "Deve retornar DashboardCandidatoResponseDTO quando o usuario autenticado for CANDIDATO"() {
        given: "Um usuario autenticado com papel CANDIDATO e perfil existente"
        Usuario usuario = new Usuario(id: 1L, email: "joao.silva@email.com", role: Role.CANDIDATO)
        Candidato candidato = new Candidato(id: 10L, nome: "João", sobrenome: "Silva", usuario: usuario)

        and: "Os repositorios e o client de competencias retornam as contagens do candidato"
        1 * candidatoRepository.findByUsuarioEmail("joao.silva@email.com") >> Optional.of(candidato)
        1 * vagaRepository.countVagasSugeridasParaCandidato(10L) >> 14L
        1 * curtidaRepository.countByCandidatoIdAndCandidatoCurtiuEmIsNotNull(10L) >> 8L
        1 * curtidaRepository.countByCandidatoIdAndEmpresaCurtiuEmIsNotNull(10L) >> 5L
        1 * curtidaRepository.countByCandidatoIdAndCandidatoCurtiuEmIsNotNullAndEmpresaCurtiuEmIsNotNull(10L) >> 2L
        1 * competenciaClient.contarCompetenciasCandidato(usuario, 10L) >> 6L

        when: "Executa a consulta do dashboard"
        Object resultado = useCase.executar(usuario)

        then: "Retorna uma instancia de DashboardCandidatoResponseDTO com os indicadores corretos"
        resultado instanceof DashboardCandidatoResponseDTO
        DashboardCandidatoResponseDTO dashboard = (DashboardCandidatoResponseDTO) resultado
        dashboard.tipoUsuario() == "CANDIDATO"
        dashboard.totalVagasSugeridas() == 14L
        dashboard.totalCurtidasEnviadas() == 8L
        dashboard.totalCurtidasRecebidas() == 5L
        dashboard.totalMatches() == 2L
        dashboard.totalCompetencias() == 6L
    }

    void "Deve retornar DashboardEmpresaResponseDTO quando o usuario autenticado for EMPRESA"() {
        given: "Um usuario autenticado com papel EMPRESA e perfil existente"
        Usuario usuario = new Usuario(id: 2L, email: "rh@techsolutions.com.br", role: Role.EMPRESA)
        Empresa empresa = new Empresa(id: 20L, nome: "Tech Solutions Ltda", usuario: usuario)
        List<CandidatosPorCompetenciaDTO> distribuicaoCompetencias = [
                new CandidatosPorCompetenciaDTO(1L, "Java", 12L),
                new CandidatosPorCompetenciaDTO(2L, "Spring Boot", 9L)
        ]

        and: "Os repositorios e o client de competencias retornam as contagens e estatisticas da empresa"
        1 * empresaRepository.findByUsuarioEmail("rh@techsolutions.com.br") >> Optional.of(empresa)
        1 * vagaRepository.countByEmpresaId(20L) >> 4L
        1 * curtidaRepository.countByVagaEmpresaIdAndCandidatoCurtiuEmIsNotNull(20L) >> 12L
        1 * curtidaRepository.countByVagaEmpresaIdAndEmpresaCurtiuEmIsNotNull(20L) >> 5L
        1 * curtidaRepository.countByVagaEmpresaIdAndCandidatoCurtiuEmIsNotNullAndEmpresaCurtiuEmIsNotNull(20L) >> 3L
        1 * competenciaClient.contarCompetenciasEmpresa(usuario, 20L) >> 5L
        1 * competenciaClient.listarCandidatosPorCompetencia(usuario, 20L) >> distribuicaoCompetencias

        when: "Executa a consulta do dashboard"
        Object resultado = useCase.executar(usuario)

        then: "Retorna uma instancia de DashboardEmpresaResponseDTO com os indicadores e o grafico de competencias"
        resultado instanceof DashboardEmpresaResponseDTO
        DashboardEmpresaResponseDTO dashboard = (DashboardEmpresaResponseDTO) resultado
        dashboard.tipoUsuario() == "EMPRESA"
        dashboard.totalVagasAtivas() == 4L
        dashboard.totalCurtidasRecebidas() == 12L
        dashboard.totalCurtidasEnviadas() == 5L
        dashboard.totalMatches() == 3L
        dashboard.totalCompetencias() == 5L
        dashboard.candidatosPorCompetencia().size() == 2
        dashboard.candidatosPorCompetencia()[0].nomeCompetencia() == "Java"
        dashboard.candidatosPorCompetencia()[0].totalCandidatos() == 12L
    }

    void "Deve lancar IllegalArgumentException quando o usuario autenticado for nulo"() {
        when: "Executa a consulta com usuario nulo"
        useCase.executar(null)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O usuário autenticado não pode ser nulo."
    }

    void "Deve lancar NoSuchElementException quando o perfil do candidato nao for encontrado"() {
        given: "Um usuario CANDIDATO sem perfil correspondente no banco"
        Usuario usuario = new Usuario(id: 99L, email: "inexistente@email.com", role: Role.CANDIDATO)
        1 * candidatoRepository.findByUsuarioEmail("inexistente@email.com") >> Optional.empty()

        when: "Executa a consulta do dashboard"
        useCase.executar(usuario)

        then: "Lanca NoSuchElementException"
        NoSuchElementException ex = thrown(NoSuchElementException)
        ex.message == "Perfil de candidato não encontrado."
    }

    void "Deve lancar NoSuchElementException quando o perfil da empresa nao for encontrado"() {
        given: "Um usuario EMPRESA sem perfil correspondente no banco"
        Usuario usuario = new Usuario(id: 98L, email: "empresa.inexistente@email.com", role: Role.EMPRESA)
        1 * empresaRepository.findByUsuarioEmail("empresa.inexistente@email.com") >> Optional.empty()

        when: "Executa a consulta do dashboard"
        useCase.executar(usuario)

        then: "Lanca NoSuchElementException"
        NoSuchElementException ex = thrown(NoSuchElementException)
        ex.message == "Perfil de empresa não encontrado."
    }

}
