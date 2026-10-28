package org.ugustavodev.msnucleo.usecase.impl.formacao

import org.ugustavodev.msnucleo.dto.request.FormacaoRequestDTO
import org.ugustavodev.msnucleo.dto.response.FormacaoResponseDTO
import org.ugustavodev.msnucleo.mapper.FormacaoMapper
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.model.Formacao
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.repositories.FormacaoRepository
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

class CadastrarFormacaoUseCaseSpec extends Specification {

    FormacaoRepository formacaoRepository = Mock(FormacaoRepository)
    CandidatoRepository candidatoRepository = Mock(CandidatoRepository)
    FormacaoMapper formacaoMapper = Mock(FormacaoMapper)

    @Subject
    CadastrarFormacaoUseCase useCase = new CadastrarFormacaoUseCase(formacaoRepository, candidatoRepository, formacaoMapper)

    void "Deve lancar excecao quando requestDTO for nulo"() {
        given: "Um request nulo e um candidatoId valido"
        FormacaoRequestDTO request = null
        Long candidatoId = 1L

        when: "Tenta cadastrar formacao com request nulo"
        useCase.executar(request, candidatoId)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O objeto FormacaoRequestDTO não pode ser nulo."
    }

    void "Deve lancar excecao quando o candidato nao for encontrado"() {
        given: "Um request valido e um candidatoId inexistente"
        FormacaoRequestDTO request = new FormacaoRequestDTO(instituicao: "USP", curso: "CC")
        Long candidatoId = 99L
        1 * candidatoRepository.findById(candidatoId) >> Optional.empty()

        when: "Tenta cadastrar a formacao"
        useCase.executar(request, candidatoId)

        then: "Lanca NoSuchElementException"
        NoSuchElementException ex = thrown(NoSuchElementException)
        ex.message == "Candidato não encontrado para o ID: ${candidatoId}"
    }

    void "Deve cadastrar a formacao academica vinculada ao candidato com sucesso"() {
        given: "Um request valido e um candidato existente"
        Long candidatoId = 1L
        LocalDate inicio = LocalDate.of(2020, 2, 1)
        LocalDate conclusao = LocalDate.of(2023, 12, 15)
        FormacaoRequestDTO request = new FormacaoRequestDTO(
                instituicao: "USP",
                curso: "Ciência da Computação",
                nivel: "Bacharelado",
                status: "Concluído",
                dataInicio: inicio,
                dataConclusao: conclusao
        )
        Candidato candidato = new Candidato(id: candidatoId)
        Formacao formacaoMapeada = new Formacao(instituicao: "USP", curso: "Ciência da Computação", candidato: candidato)
        Formacao formacaoSalva = new Formacao(id: 10L, instituicao: "USP", curso: "Ciência da Computação", candidato: candidato)
        FormacaoResponseDTO esperado = new FormacaoResponseDTO(10L, "USP", "Ciência da Computação", "Bacharelado", "Concluído", inicio, conclusao)

        1 * candidatoRepository.findById(candidatoId) >> Optional.of(candidato)
        1 * formacaoMapper.fromRequestDTOToEntity(request, candidato) >> formacaoMapeada
        1 * formacaoRepository.save(formacaoMapeada) >> formacaoSalva
        1 * formacaoMapper.toResponseDTO(formacaoSalva) >> esperado

        when: "Executa o cadastro de formacao"
        FormacaoResponseDTO resultado = useCase.executar(request, candidatoId)

        then: "Retorna o FormacaoResponseDTO esperado"
        resultado == esperado
    }
}
