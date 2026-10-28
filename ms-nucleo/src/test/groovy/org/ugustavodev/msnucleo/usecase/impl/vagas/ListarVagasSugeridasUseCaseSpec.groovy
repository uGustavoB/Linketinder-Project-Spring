package org.ugustavodev.msnucleo.usecase.impl.vagas

import org.ugustavodev.msnucleo.dto.response.VagaPublicoResponseDTO
import org.ugustavodev.msnucleo.mapper.VagaMapper
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.VagaRepository
import spock.lang.Specification
import spock.lang.Subject

class ListarVagasSugeridasUseCaseSpec extends Specification {

    VagaRepository vagaRepository = Mock(VagaRepository)
    VagaMapper vagaMapper = Mock(VagaMapper)

    @Subject
    ListarVagasSugeridasUseCase useCase = new ListarVagasSugeridasUseCase(vagaRepository, vagaMapper)

    void "Deve lancar excecao quando candidatoId for nulo"() {
        given: "Um candidatoId nulo"
        Long candidatoId = null

        when: "Executa a listagem de vagas sugeridas"
        useCase.executar(candidatoId)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID do candidato não pode ser nulo"
    }

    void "Deve retornar a lista de vagas sugeridas ainda nao curtidas pelo candidato"() {
        given: "Um candidatoId valido e uma vaga sugerida"
        Long candidatoId = 10L
        Vaga vaga = new Vaga(id: 5L, nome: "Dev Groovy")
        VagaPublicoResponseDTO dto = new VagaPublicoResponseDTO(5L, "Dev Groovy", "Desc", "Remoto", "Pleno", 1L)

        1 * vagaRepository.findVagasSugeridasParaCandidato(candidatoId) >> [vaga]
        1 * vagaMapper.toPublicoResponseDTO(vaga) >> dto

        when: "Executa a listagem de vagas sugeridas"
        List<VagaPublicoResponseDTO> resultado = useCase.executar(candidatoId)

        then: "Retorna a lista mapeada para VagaPublicoResponseDTO"
        resultado.size() == 1
        resultado[0] == dto
    }

}
