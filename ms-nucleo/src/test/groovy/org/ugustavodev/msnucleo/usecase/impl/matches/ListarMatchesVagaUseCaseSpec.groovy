package org.ugustavodev.msnucleo.usecase.impl.matches

import org.springframework.security.access.AccessDeniedException
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.dto.response.CandidatoPublicoResponseDTO
import org.ugustavodev.msnucleo.dto.response.MatchVagaResponseDTO
import org.ugustavodev.msnucleo.mapper.CandidatoMapper
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.model.Curtida
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.CurtidaRepository
import org.ugustavodev.msnucleo.repositories.VagaRepository
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class ListarMatchesVagaUseCaseSpec extends Specification {

    CurtidaRepository curtidaRepository = Mock(CurtidaRepository)
    VagaRepository vagaRepository = Mock(VagaRepository)
    CandidatoMapper candidatoMapper = Mock(CandidatoMapper)

    @Subject
    ListarMatchesVagaUseCase useCase = new ListarMatchesVagaUseCase(curtidaRepository, vagaRepository, candidatoMapper)

    void "Deve lancar AccessDeniedException quando a vaga pertencer a outra empresa"() {
        given: "Uma vaga da empresa 1 consultada pela empresa 2"
        Long idVaga = 5L
        Long idEmpresaLogada = 2L
        Vaga vaga = new Vaga(id: idVaga, empresa: new Empresa(id: 1L))
        1 * vagaRepository.findById(idVaga) >> Optional.of(vaga)

        when: "Executa a listagem de matches da vaga"
        useCase.executar(idVaga, idEmpresaLogada)

        then: "Lanca AccessDeniedException"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Esta vaga não pertence à sua empresa."
    }

    void "Deve listar os matches da vaga revelando nome completo e email do candidato"() {
        given: "Uma vaga da empresa logada com match confirmado"
        Long idVaga = 5L
        Long idEmpresaLogada = 1L
        Vaga vaga = new Vaga(id: idVaga, empresa: new Empresa(id: idEmpresaLogada))
        Candidato candidato = new Candidato(
                id: 10L,
                nome: "João",
                sobrenome: "Silva",
                pais: "Brasil",
                estado: "SP",
                descricao: "Dev Backend",
                usuario: new Usuario(email: "joao.silva@email.com")
        )
        LocalDateTime dataCandidato = LocalDateTime.of(2026, 10, 23, 8, 30, 0)
        LocalDateTime dataEmpresa = LocalDateTime.of(2026, 10, 23, 9, 15, 0)
        Curtida curtida = new Curtida(id: 100L, vaga: vaga, candidato: candidato, candidatoCurtiuEm: dataCandidato, empresaCurtiuEm: dataEmpresa)
        CandidatoPublicoResponseDTO candidatoDTO = new CandidatoPublicoResponseDTO(10L, "Brasil", "SP", "Dev Backend", [])

        1 * vagaRepository.findById(idVaga) >> Optional.of(vaga)
        1 * curtidaRepository.findMatchesByVagaIdAndEmpresaId(idVaga, idEmpresaLogada) >> [curtida]
        1 * candidatoMapper.toPublicoResponseDTO(candidato) >> candidatoDTO

        when: "Executa a listagem de matches da vaga"
        List<MatchVagaResponseDTO> resultado = useCase.executar(idVaga, idEmpresaLogada)

        then: "Retorna os dados publicos acrescidos de nome completo, email e dataMatch"
        resultado.size() == 1
        resultado[0].candidato() == candidatoDTO
        resultado[0].candidatoNome() == "João Silva"
        resultado[0].candidatoEmail() == "joao.silva@email.com"
        resultado[0].dataMatch() == dataEmpresa
    }

}
