package org.ugustavodev.msnucleo.usecase.impl.vagas

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.CurtidaRepository
import org.ugustavodev.msnucleo.repositories.VagaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.vagas.IDeletarVagaUseCase

@Service
class DeletarVagaUseCase implements IDeletarVagaUseCase {

    private final VagaRepository vagaRepository
    private final CurtidaRepository curtidaRepository

    DeletarVagaUseCase(VagaRepository vagaRepository, CurtidaRepository curtidaRepository) {
        this.vagaRepository = vagaRepository
        this.curtidaRepository = curtidaRepository
    }

    @Override
    @Transactional
    void executar(Long vagaId, Long empresaId) {
        if (vagaId == null) {
            throw new IllegalArgumentException("O ID da vaga não pode ser nulo.")
        }

        Optional<Vaga> vagaExistente = vagaRepository.findById(vagaId);

        if (vagaExistente.isEmpty()) {
            throw new NoSuchElementException("Vaga não encontrada para o ID: ${vagaId}")
        }

        if (vagaExistente.get().empresa.id != empresaId) {
            throw new IllegalArgumentException("Você não tem permissão para deletar esta vaga.")
        }

        curtidaRepository.deleteByVagaId(vagaId)
        vagaRepository.deleteById(vagaId)
    }

}
