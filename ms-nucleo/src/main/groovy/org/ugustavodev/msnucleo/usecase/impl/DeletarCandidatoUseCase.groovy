package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IDeletarCandidatoUseCase

@Service
class DeletarCandidatoUseCase implements IDeletarCandidatoUseCase {
    private final CandidatoRepository candidatoRepository

    DeletarCandidatoUseCase(CandidatoRepository candidatoRepository) {
        this.candidatoRepository = candidatoRepository
    }

    @Override
    void executar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O ID do candidato não pode ser nulo.")
        }

        if (!candidatoRepository.existsById(id)) {
            throw new NoSuchElementException("Candidato não encontrado.")
        }

        candidatoRepository.deleteById(id)
    }
}
