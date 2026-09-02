package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.repositories.VagaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IDeletarVagaUseCase

@Service
class DeletarVagaUseCase implements IDeletarVagaUseCase {
    private final VagaRepository vagaRepository

    DeletarVagaUseCase(VagaRepository vagaRepository) {
        this.vagaRepository = vagaRepository
    }

    @Override
    void executar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O ID da vaga não pode ser nulo.")
        }

        if (!vagaRepository.existsById(id)) {
            throw new NoSuchElementException("Vaga não encontrada para o ID: ${id}")
        }

        vagaRepository.deleteById(id)
    }
}
