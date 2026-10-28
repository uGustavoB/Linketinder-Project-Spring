package org.ugustavodev.msnucleo.usecase.impl.vagas

import org.ugustavodev.msnucleo.dto.response.VagaPublicoResponseDTO
import org.ugustavodev.msnucleo.mapper.VagaMapper
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.VagaRepository
import spock.lang.Specification
import spock.lang.Subject

class ListarVagasUseCaseSpec extends Specification {

    VagaRepository vagaRepository = Mock(VagaRepository)
    VagaMapper vagaMapper = Mock(VagaMapper)

    @Subject
    ListarVagasUseCase useCase = new ListarVagasUseCase(vagaRepository, vagaMapper)

    void "Deve listar apenas as vagas da empresa quando empresaId for informado"() {
        given: "Um empresaId valido e uma vaga pertencente a ela"
        Long empresaId = 1L
        Vaga vaga = new Vaga(id: 5L, nome: "Dev Groovy")
        VagaPublicoResponseDTO dto = new VagaPublicoResponseDTO(5L, "Dev Groovy", "Desc", "Remoto", "Pleno", empresaId)

        1 * vagaRepository.findByEmpresaId(empresaId) >> [vaga]
        1 * vagaMapper.toPublicoResponseDTO(vaga) >> dto
        0 * vagaRepository.findAll()

        when: "Executa a listagem passando empresaId"
        List<VagaPublicoResponseDTO> resultado = useCase.executar(empresaId)

        then: "Retorna apenas as vagas filtradas"
        resultado.size() == 1
        resultado[0] == dto
    }

    void "Deve listar todas as vagas quando empresaId for nulo"() {
        given: "Um empresaId nulo e duas vagas cadastradas"
        Long empresaId = null
        Vaga v1 = new Vaga(id: 5L, nome: "Dev Groovy")
        Vaga v2 = new Vaga(id: 6L, nome: "Dev Angular")
        VagaPublicoResponseDTO dto1 = new VagaPublicoResponseDTO(5L, "Dev Groovy", "Desc 1", "Remoto", "Pleno", 1L)
        VagaPublicoResponseDTO dto2 = new VagaPublicoResponseDTO(6L, "Dev Angular", "Desc 2", "Híbrido", "Júnior", 2L)

        1 * vagaRepository.findAll() >> [v1, v2]
        1 * vagaMapper.toPublicoResponseDTO(v1) >> dto1
        1 * vagaMapper.toPublicoResponseDTO(v2) >> dto2

        when: "Executa a listagem sem filtro"
        List<VagaPublicoResponseDTO> resultado = useCase.executar(empresaId)

        then: "Retorna todas as vagas"
        resultado.size() == 2
        resultado == [dto1, dto2]
    }

}
