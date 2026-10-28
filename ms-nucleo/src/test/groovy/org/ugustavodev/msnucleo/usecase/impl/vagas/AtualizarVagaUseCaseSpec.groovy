package org.ugustavodev.msnucleo.usecase.impl.vagas

import org.springframework.security.access.AccessDeniedException
import org.ugustavodev.msnucleo.dto.request.VagaRequestDTO
import org.ugustavodev.msnucleo.dto.response.VagaResponseDTO
import org.ugustavodev.msnucleo.mapper.VagaMapper
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.repositories.VagaRepository
import spock.lang.Specification
import spock.lang.Subject

class AtualizarVagaUseCaseSpec extends Specification {

    VagaRepository vagaRepository = Mock(VagaRepository)
    EmpresaRepository empresaRepository = Mock(EmpresaRepository)
    VagaMapper vagaMapper = Mock(VagaMapper)

    @Subject
    AtualizarVagaUseCase useCase = new AtualizarVagaUseCase(vagaRepository, empresaRepository, vagaMapper)

    void "Deve lancar excecao quando o ID da vaga for nulo"() {
        given: "Um ID nulo"
        Long id = null
        VagaRequestDTO request = new VagaRequestDTO()
        Long empresaId = 1L

        when: "Tenta atualizar com ID nulo"
        useCase.executar(id, request, empresaId)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID da vaga não pode ser nulo."
    }

    void "Deve lancar excecao quando a vaga pertencer a outra empresa"() {
        given: "Uma vaga pertencente a empresa 1 e tentativa de edicao pela empresa 2"
        Long id = 5L
        Long empresaDonaId = 1L
        Long outraEmpresaId = 2L
        Vaga vagaExistente = new Vaga(id: id, empresa: new Empresa(id: empresaDonaId))
        VagaRequestDTO request = new VagaRequestDTO(nome: "Vaga Editada")

        1 * vagaRepository.findById(id) >> Optional.of(vagaExistente)

        when: "A empresa 2 tenta atualizar a vaga"
        useCase.executar(id, request, outraEmpresaId)

        then: "Lanca AccessDeniedException"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Você não tem permissão para editar esta vaga."
        0 * vagaRepository.save(_)
    }

    void "Deve atualizar a vaga com sucesso quando pertencer a empresa autenticada"() {
        given: "Uma vaga pertencente a empresa logada"
        Long id = 5L
        Long empresaId = 1L
        Empresa empresa = new Empresa(id: empresaId, nome: "Tech Solutions Ltda")
        Vaga vagaExistente = new Vaga(id: id, nome: "Dev Júnior", descricao: "Antiga", local: "Presencial", nivel: "Júnior", empresa: empresa)
        VagaRequestDTO request = new VagaRequestDTO(nome: "Dev Pleno", descricao: "Nova", local: "Remoto", nivel: "Pleno")
        VagaResponseDTO esperado = new VagaResponseDTO(id, "Dev Pleno", "Nova", "Remoto", "Pleno", empresaId, "Tech Solutions Ltda")

        1 * vagaRepository.findById(id) >> Optional.of(vagaExistente)
        1 * vagaRepository.save(vagaExistente) >> vagaExistente
        1 * vagaMapper.toResponseDTO(vagaExistente) >> esperado

        when: "Executa a atualizacao da vaga"
        VagaResponseDTO resultado = useCase.executar(id, request, empresaId)

        then: "Os dados da vaga sao atualizados e retorna o VagaResponseDTO"
        vagaExistente.nome == "Dev Pleno"
        vagaExistente.local == "Remoto"
        vagaExistente.nivel == "Pleno"
        resultado == esperado
    }

}
