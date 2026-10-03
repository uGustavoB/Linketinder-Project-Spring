package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.model.Formacao
import org.ugustavodev.msnucleo.repositories.FormacaoRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IDeletarFormacaoUseCase

@Service
class DeletarFormacaoUseCase implements IDeletarFormacaoUseCase {
    private final FormacaoRepository formacaoRepository

    DeletarFormacaoUseCase(FormacaoRepository formacaoRepository) {
        this.formacaoRepository = formacaoRepository
    }

    @Override
    void executar(Long id, Long candidatoId) {
        Formacao formacaoExistente = formacaoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Formação não encontrada para o ID: ${id}"))

        if (formacaoExistente.candidato == null || formacaoExistente.candidato.id != candidatoId) {
            throw new IllegalArgumentException("Você não tem permissão para deletar esta formação.")
        }

        formacaoRepository.delete(formacaoExistente)
    }
}
