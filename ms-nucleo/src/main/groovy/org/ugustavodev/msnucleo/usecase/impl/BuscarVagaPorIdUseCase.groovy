package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.response.VagaResponseDTO
import org.ugustavodev.msnucleo.mapper.VagaMapper
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.VagaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IBuscarVagaPorIdUseCase

@Service
class BuscarVagaPorIdUseCase implements IBuscarVagaPorIdUseCase {
    private final VagaRepository vagaRepository
    private final VagaMapper vagaMapper

    BuscarVagaPorIdUseCase(VagaRepository vagaRepository, VagaMapper vagaMapper) {
        this.vagaRepository = vagaRepository
        this.vagaMapper = vagaMapper
    }

    @Override
    VagaResponseDTO executar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O ID da vaga não pode ser nulo.")
        }

        Vaga vaga = vagaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Vaga não encontrada para o ID: ${id}"))

        return vagaMapper.toResponseDTO(vaga)
    }
}
