package org.ugustavodev.msnucleo.usecase.impl.matches

import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.dto.response.MatchCandidatoResponseDTO
import org.ugustavodev.msnucleo.dto.response.VagaPublicoResponseDTO
import org.ugustavodev.msnucleo.mapper.VagaMapper
import org.ugustavodev.msnucleo.model.Curtida
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.CurtidaRepository
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class ListarMatchesCandidatoUseCaseSpec extends Specification {

    CurtidaRepository curtidaRepository = Mock(CurtidaRepository)
    VagaMapper vagaMapper = Mock(VagaMapper)

    @Subject
    ListarMatchesCandidatoUseCase useCase = new ListarMatchesCandidatoUseCase(curtidaRepository, vagaMapper)

    void "Deve lancar excecao quando idCandidato for nulo"() {
        given: "Um idCandidato nulo"
        Long idCandidato = null

        when: "Executa a listagem de matches"
        useCase.executar(idCandidato)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID do candidato não pode ser nulo."
    }

    void "Deve listar os matches do candidato revelando nome e email da empresa"() {
        given: "Um candidato com um match consolidado"
        Long idCandidato = 10L
        Empresa empresa = new Empresa(
                id: 1L,
                nome: "Tech Solutions Ltda",
                pais: "Brasil",
                estado: "SP",
                descricao: "Consultoria TI",
                usuario: new Usuario(email: "rh@techsolutions.com.br")
        )
        Vaga vaga = new Vaga(id: 5L, nome: "Dev Groovy", empresa: empresa)
        LocalDateTime dataEmpresa = LocalDateTime.of(2026, 10, 23, 8, 0, 0)
        LocalDateTime dataCandidato = LocalDateTime.of(2026, 10, 23, 9, 30, 0)
        Curtida curtida = new Curtida(id: 100L, vaga: vaga, candidatoCurtiuEm: dataCandidato, empresaCurtiuEm: dataEmpresa)
        VagaPublicoResponseDTO vagaDTO = new VagaPublicoResponseDTO(5L, "Dev Groovy", "Desc", "Remoto", "Pleno", 1L)

        1 * curtidaRepository.findMatchesByCandidatoId(idCandidato) >> [curtida]
        1 * vagaMapper.toPublicoResponseDTO(vaga) >> vagaDTO

        when: "Executa a listagem de matches do candidato"
        List<MatchCandidatoResponseDTO> resultado = useCase.executar(idCandidato)

        then: "Retorna os dados da vaga, os contatos revelados da empresa e dataMatch mais recente"
        resultado.size() == 1
        resultado[0].vaga() == vagaDTO
        resultado[0].empresaNome() == "Tech Solutions Ltda"
        resultado[0].empresaEmail() == "rh@techsolutions.com.br"
        resultado[0].dataMatch() == dataCandidato
    }
}
