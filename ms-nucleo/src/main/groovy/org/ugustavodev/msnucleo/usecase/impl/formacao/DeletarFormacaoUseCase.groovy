package org.ugustavodev.msnucleo.usecase.impl.formacao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.ugustavodev.msnucleo.model.Formacao
import org.ugustavodev.msnucleo.repositories.FormacaoRepository
import org.ugustavodev.msnucleo.usecase.interfaces.formacao.IDeletarFormacaoUseCase

@Service
class DeletarFormacaoUseCase implements IDeletarFormacaoUseCase {

    private final FormacaoRepository formacaoRepository

    DeletarFormacaoUseCase(FormacaoRepository formacaoRepository) {
        this.formacaoRepository = formacaoRepository
    }

    @Override
    @Transactional
    void executar(Long id, Long candidatoId) {
        if (id == null) {
            throw new IllegalArgumentException("O ID da formação não pode ser nulo.")
        }

        Formacao formacaoExistente = formacaoRepository.findById(id)
                .orElseThrow { new NoSuchElementException("Formação não encontrada para o ID: ${id}") }

        if (formacaoExistente.candidato == null || formacaoExistente.candidato.id != candidatoId) {
            throw new IllegalArgumentException("Você não tem permissão para deletar esta formação.")
        }

        formacaoRepository.delete(formacaoExistente)
    }

}
