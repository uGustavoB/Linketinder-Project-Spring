package org.ugustavodev.msnucleo.usecase.impl.formacao

import org.ugustavodev.msnucleo.dto.request.FormacaoRequestDTO
import org.ugustavodev.msnucleo.dto.response.FormacaoResponseDTO
import org.ugustavodev.msnucleo.mapper.FormacaoMapper
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.model.Formacao
import org.ugustavodev.msnucleo.repositories.FormacaoRepository
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

class AtualizarFormacaoUseCaseSpec extends Specification {

    FormacaoRepository formacaoRepository = Mock(FormacaoRepository)
    FormacaoMapper formacaoMapper = Mock(FormacaoMapper)

    @Subject
    AtualizarFormacaoUseCase useCase = new AtualizarFormacaoUseCase(formacaoRepository, formacaoMapper)

    void "Deve lancar excecao quando requestDTO for nulo"() {
        given: "Um request nulo"
        Long id = 1L
        FormacaoRequestDTO request = null
        Long candidatoId = 1L

        when: "Tenta atualizar com request nulo"
        useCase.executar(id, request, candidatoId)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O objeto FormacaoRequestDTO não pode ser nulo."
    }

    void "Deve lancar excecao quando a formacao nao for encontrada"() {
        given: "Um ID de formacao inexistente"
        Long id = 99L
        FormacaoRequestDTO request = new FormacaoRequestDTO(instituicao: "USP")
        Long candidatoId = 1L
        1 * formacaoRepository.findById(id) >> Optional.empty()

        when: "Tenta atualizar a formacao"
        useCase.executar(id, request, candidatoId)

        then: "Lanca NoSuchElementException"
        NoSuchElementException ex = thrown(NoSuchElementException)
        ex.message == "Formação não encontrada para o ID: ${id}"
    }

    void "Deve lancar excecao quando a formacao pertencer a outro candidato"() {
        given: "Uma formacao pertencente ao candidato 1 e tentativa de edicao pelo candidato 2"
        Long id = 10L
        Long candidatoDonoId = 1L
        Long outroCandidatoId = 2L
        Formacao formacaoExistente = new Formacao(id: id, candidato: new Candidato(id: candidatoDonoId))
        FormacaoRequestDTO request = new FormacaoRequestDTO(instituicao: "UNICAMP")
        1 * formacaoRepository.findById(id) >> Optional.of(formacaoExistente)

        when: "Outro candidato tenta editar a formacao"
        useCase.executar(id, request, outroCandidatoId)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "Você não tem permissão para editar esta formação."
    }

    void "Deve atualizar a formacao com sucesso quando pertencer ao candidato autenticado"() {
        given: "Uma formacao pertencente ao candidato logado"
        Long id = 10L
        Long candidatoId = 1L
        LocalDate inicio = LocalDate.of(2021, 1, 1)
        LocalDate conclusao = LocalDate.of(2024, 12, 1)
        Formacao formacaoExistente = new Formacao(
                id: id,
                instituicao: "USP",
                curso: "Sistemas",
                candidato: new Candidato(id: candidatoId)
        )
        FormacaoRequestDTO request = new FormacaoRequestDTO(
                instituicao: "UNICAMP",
                curso: "Engenharia de Computação",
                nivel: "Bacharelado",
                status: "Concluído",
                dataInicio: inicio,
                dataConclusao: conclusao
        )
        FormacaoResponseDTO esperado = new FormacaoResponseDTO(id, "UNICAMP", "Engenharia de Computação", "Bacharelado", "Concluído", inicio, conclusao)

        1 * formacaoRepository.findById(id) >> Optional.of(formacaoExistente)
        1 * formacaoRepository.save(formacaoExistente) >> formacaoExistente
        1 * formacaoMapper.toResponseDTO(formacaoExistente) >> esperado

        when: "Executa a atualizacao"
        FormacaoResponseDTO resultado = useCase.executar(id, request, candidatoId)

        then: "Os campos da entidade sao atualizados e retorna o DTO esperado"
        formacaoExistente.instituicao == "UNICAMP"
        formacaoExistente.curso == "Engenharia de Computação"
        resultado == esperado
    }

}
