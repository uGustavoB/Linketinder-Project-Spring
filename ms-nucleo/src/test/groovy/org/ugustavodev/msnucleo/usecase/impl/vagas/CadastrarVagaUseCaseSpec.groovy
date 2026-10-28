package org.ugustavodev.msnucleo.usecase.impl.vagas

import org.ugustavodev.msnucleo.dto.request.VagaRequestDTO
import org.ugustavodev.msnucleo.dto.response.VagaResponseDTO
import org.ugustavodev.msnucleo.mapper.VagaMapper
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.repositories.VagaRepository
import spock.lang.Specification
import spock.lang.Subject

class CadastrarVagaUseCaseSpec extends Specification {

    VagaRepository vagaRepository = Mock(VagaRepository)
    EmpresaRepository empresaRepository = Mock(EmpresaRepository)
    VagaMapper vagaMapper = Mock(VagaMapper)

    @Subject
    CadastrarVagaUseCase useCase = new CadastrarVagaUseCase(vagaRepository, empresaRepository, vagaMapper)

    void "Deve lancar excecao quando requestDTO for nulo"() {
        given: "Um request nulo e um empresaId valido"
        VagaRequestDTO request = null
        Long empresaId = 1L

        when: "Tenta cadastrar vaga com request nulo"
        useCase.executar(request, empresaId)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O objeto VagaRequestDTO não pode ser nulo."
    }

    void "Deve lancar excecao quando empresaId for nulo"() {
        given: "Um request valido e um empresaId nulo"
        VagaRequestDTO request = new VagaRequestDTO(nome: "Dev Groovy")
        Long empresaId = null

        when: "Tenta cadastrar vaga com empresaId nulo"
        useCase.executar(request, empresaId)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID da empresa não pode ser nulo."
    }

    void "Deve lancar excecao quando a empresa nao for encontrada"() {
        given: "Um empresaId inexistente"
        VagaRequestDTO request = new VagaRequestDTO(nome: "Dev Groovy")
        Long empresaId = 99L
        1 * empresaRepository.findById(empresaId) >> Optional.empty()

        when: "Tenta cadastrar a vaga"
        useCase.executar(request, empresaId)

        then: "Lanca NoSuchElementException"
        NoSuchElementException ex = thrown(NoSuchElementException)
        ex.message == "Empresa não encontrada para o ID: ${empresaId}"
    }

    void "Deve cadastrar a vaga vinculada a empresa com sucesso"() {
        given: "Uma empresa existente e um VagaRequestDTO valido"
        Long empresaId = 1L
        Empresa empresa = new Empresa(id: empresaId, nome: "Tech Solutions Ltda")
        VagaRequestDTO request = new VagaRequestDTO(
                nome: "Dev Backend Groovy",
                descricao: "APIs REST com Spring Boot",
                local: "Remoto",
                nivel: "Pleno"
        )
        Vaga vagaMapeada = new Vaga(nome: "Dev Backend Groovy", empresa: empresa)
        Vaga vagaSalva = new Vaga(id: 5L, nome: "Dev Backend Groovy", empresa: empresa)
        VagaResponseDTO esperado = new VagaResponseDTO(5L, "Dev Backend Groovy", "APIs REST com Spring Boot", "Remoto", "Pleno", empresaId, "Tech Solutions Ltda")

        1 * empresaRepository.findById(empresaId) >> Optional.of(empresa)
        1 * vagaMapper.fromRequestDTOToEntity(request, empresa) >> vagaMapeada
        1 * vagaRepository.save(vagaMapeada) >> vagaSalva
        1 * vagaMapper.toResponseDTO(vagaSalva) >> esperado

        when: "Executa o cadastro da vaga"
        VagaResponseDTO resultado = useCase.executar(request, empresaId)

        then: "Retorna o VagaResponseDTO esperado"
        resultado == esperado
    }
}
