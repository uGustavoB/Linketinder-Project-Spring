package org.ugustavodev.msnucleo.usecase.impl.matches

import org.springframework.security.access.AccessDeniedException
import org.ugustavodev.msnucleo.dto.response.CandidatoPublicoResponseDTO
import org.ugustavodev.msnucleo.mapper.CandidatoMapper
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.repositories.VagaRepository
import spock.lang.Specification
import spock.lang.Subject

class ListarCandidatosInteressadosUseCaseSpec extends Specification {

    CandidatoRepository candidatoRepository = Mock(CandidatoRepository)
    VagaRepository vagaRepository = Mock(VagaRepository)
    CandidatoMapper candidatoMapper = Mock(CandidatoMapper)

    @Subject
    ListarCandidatosInteressadosUseCase useCase = new ListarCandidatosInteressadosUseCase(
            candidatoRepository,
            vagaRepository,
            candidatoMapper
    )

    void "Deve lancar AccessDeniedException quando a vaga nao pertencer a empresa autenticada"() {
        given: "Uma vaga da empresa 1 e tentativa de consulta pela empresa 2"
        Long vagaId = 5L
        Long empresaId = 2L
        Vaga vaga = new Vaga(id: vagaId, empresa: new Empresa(id: 1L))
        1 * vagaRepository.findById(vagaId) >> Optional.of(vaga)

        when: "Executa a listagem de interessados"
        useCase.executar(vagaId, empresaId)

        then: "Lanca AccessDeniedException"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "A vaga informada não pertence à empresa autenticada"
    }

    void "Deve retornar candidatos interessados de forma anonimizada para a vaga da empresa"() {
        given: "Uma vaga pertencente a empresa logada e um candidato interessado"
        Long vagaId = 5L
        Long empresaId = 1L
        Vaga vaga = new Vaga(id: vagaId, empresa: new Empresa(id: empresaId))
        Candidato candidato = new Candidato(id: 10L, pais: "Brasil", estado: "SP", descricao: "Dev Backend")
        CandidatoPublicoResponseDTO dto = new CandidatoPublicoResponseDTO(10L, "Brasil", "SP", "Dev Backend", [])

        1 * vagaRepository.findById(vagaId) >> Optional.of(vaga)
        1 * candidatoRepository.findCandidatosInteressadosNaVaga(vagaId) >> [candidato]
        1 * candidatoMapper.toPublicoResponseDTO(candidato) >> dto

        when: "Executa a listagem de candidatos interessados"
        List<CandidatoPublicoResponseDTO> resultado = useCase.executar(vagaId, empresaId)

        then: "Retorna a lista anonimizada"
        resultado.size() == 1
        resultado[0] == dto
    }

}
