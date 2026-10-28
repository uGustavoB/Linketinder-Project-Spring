package org.ugustavodev.msnucleo.usecase.impl.matches

import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.dto.response.CandidatoPublicoResponseDTO
import org.ugustavodev.msnucleo.dto.response.MatchEmpresaResponseDTO
import org.ugustavodev.msnucleo.dto.response.VagaPublicoResponseDTO
import org.ugustavodev.msnucleo.mapper.CandidatoMapper
import org.ugustavodev.msnucleo.mapper.VagaMapper
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.model.Curtida
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.CurtidaRepository
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class ListarMatchesEmpresaUseCaseSpec extends Specification {

    CurtidaRepository curtidaRepository = Mock(CurtidaRepository)
    VagaMapper vagaMapper = Mock(VagaMapper)
    CandidatoMapper candidatoMapper = Mock(CandidatoMapper)

    @Subject
    ListarMatchesEmpresaUseCase useCase = new ListarMatchesEmpresaUseCase(
            curtidaRepository,
            vagaMapper,
            candidatoMapper
    )

    void "Deve lancar excecao quando o ID da empresa for nulo"() {
        given: "Um ID de empresa nulo"
        Long idEmpresa = null

        when: "Tenta executar com idEmpresa nulo"
        useCase.executar(idEmpresa)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID da empresa não pode ser nulo."

        and: "O repositorio nao deve ser consultado"
        0 * curtidaRepository.findMatchesByEmpresaId(_)
    }

    void "Deve listar os matches da empresa com detalhes da vaga e do candidato"() {
        given: "Uma empresa logada com um match consolidado"
        Long empresaId = 1L
        Empresa empresa = new Empresa(id: empresaId, nome: "Tech Solutions Ltda")
        Vaga vaga = new Vaga(
                id: 5L,
                nome: "Pessoa Desenvolvedora Backend Groovy/Spring",
                descricao: "Atuação no desenvolvimento e manutenção de microsserviços RESTful.",
                local: "São Paulo - SP (Remoto)",
                nivel: "Pleno",
                empresa: empresa
        )
        Candidato candidato = new Candidato(
                id: 10L,
                nome: "João",
                sobrenome: "Silva",
                pais: "Brasil",
                estado: "SP",
                descricao: "Desenvolvedor Backend",
                usuario: new Usuario(email: "joao.silva@email.com")
        )

        LocalDateTime candidatoCurtiu = LocalDateTime.of(2026, 10, 23, 8, 30, 0)
        LocalDateTime empresaCurtiu = LocalDateTime.of(2026, 10, 23, 9, 15, 0)

        Curtida curtida = new Curtida(
                id: 100L,
                vaga: vaga,
                candidato: candidato,
                candidatoCurtiuEm: candidatoCurtiu,
                empresaCurtiuEm: empresaCurtiu
        )

        VagaPublicoResponseDTO vagaPublicoDTO = new VagaPublicoResponseDTO(
                5L,
                "Pessoa Desenvolvedora Backend Groovy/Spring",
                "Atuação no desenvolvimento e manutenção de microsserviços RESTful.",
                "São Paulo - SP (Remoto)",
                "Pleno",
                empresaId
        )

        CandidatoPublicoResponseDTO candidatoPublicoDTO = new CandidatoPublicoResponseDTO(
                10L,
                "Brasil",
                "SP",
                "Desenvolvedor Backend",
                []
        )

        and: "Os mocks retornam os objetos esperados"
        1 * curtidaRepository.findMatchesByEmpresaId(empresaId) >> [curtida]
        1 * vagaMapper.toPublicoResponseDTO(vaga) >> vagaPublicoDTO
        1 * candidatoMapper.toPublicoResponseDTO(candidato) >> candidatoPublicoDTO

        when: "Executa a listagem de matches da empresa"
        List<MatchEmpresaResponseDTO> resultado = useCase.executar(empresaId)

        then: "Retorna a lista de matches com detalhes da vaga, candidato e dataMatch mais recente"
        resultado.size() == 1
        resultado[0].vaga() == vagaPublicoDTO
        resultado[0].candidato() == candidatoPublicoDTO
        resultado[0].candidatoNome() == "João Silva"
        resultado[0].candidatoEmail() == "joao.silva@email.com"
        resultado[0].dataMatch() == empresaCurtiu
    }
}
