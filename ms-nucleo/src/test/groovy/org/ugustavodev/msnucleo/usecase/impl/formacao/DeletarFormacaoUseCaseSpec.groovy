package org.ugustavodev.msnucleo.usecase.impl.formacao

import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.model.Formacao
import org.ugustavodev.msnucleo.repositories.FormacaoRepository
import spock.lang.Specification
import spock.lang.Subject

class DeletarFormacaoUseCaseSpec extends Specification {

    FormacaoRepository formacaoRepository = Mock(FormacaoRepository)

    @Subject
    DeletarFormacaoUseCase useCase = new DeletarFormacaoUseCase(formacaoRepository)

    void "Deve lancar excecao quando a formacao nao for encontrada"() {
        given: "Um ID de formacao inexistente"
        Long id = 99L
        Long candidatoId = 1L
        1 * formacaoRepository.findById(id) >> Optional.empty()

        when: "Tenta deletar a formacao"
        useCase.executar(id, candidatoId)

        then: "Lanca NoSuchElementException"
        NoSuchElementException ex = thrown(NoSuchElementException)
        ex.message == "Formação não encontrada para o ID: ${id}"
    }

    void "Deve lancar excecao quando a formacao pertencer a outro candidato"() {
        given: "Uma formacao pertencente ao candidato 1"
        Long id = 10L
        Long candidatoDonoId = 1L
        Long outroCandidatoId = 2L
        Formacao formacaoExistente = new Formacao(id: id, candidato: new Candidato(id: candidatoDonoId))
        1 * formacaoRepository.findById(id) >> Optional.of(formacaoExistente)

        when: "O candidato 2 tenta deletar a formacao"
        useCase.executar(id, outroCandidatoId)

        then: "Lanca IllegalArgumentException e nao deleta"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "Você não tem permissão para deletar esta formação."
        0 * formacaoRepository.delete(_)
    }

    void "Deve deletar a formacao com sucesso quando pertencer ao candidato titular"() {
        given: "Uma formacao pertencente ao candidato autenticado"
        Long id = 10L
        Long candidatoId = 1L
        Formacao formacaoExistente = new Formacao(id: id, candidato: new Candidato(id: candidatoId))
        1 * formacaoRepository.findById(id) >> Optional.of(formacaoExistente)

        when: "Executa a exclusao da formacao"
        useCase.executar(id, candidatoId)

        then: "O repositorio remove a formacao"
        1 * formacaoRepository.delete(formacaoExistente)
    }

}
