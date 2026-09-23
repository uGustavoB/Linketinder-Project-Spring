package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.request.VagaRequestDTO
import org.ugustavodev.msnucleo.dto.response.VagaResponseDTO
import org.ugustavodev.msnucleo.mapper.VagaMapper
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.repositories.VagaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IAtualizarVagaUseCase

@Service
class AtualizarVagaUseCase implements IAtualizarVagaUseCase {
    private final VagaRepository vagaRepository
    private final EmpresaRepository empresaRepository
    private final VagaMapper vagaMapper

    AtualizarVagaUseCase(VagaRepository vagaRepository, EmpresaRepository empresaRepository, VagaMapper vagaMapper) {
        this.vagaRepository = vagaRepository
        this.empresaRepository = empresaRepository
        this.vagaMapper = vagaMapper
    }

    @Override
    VagaResponseDTO executar(Long id, VagaRequestDTO requestDTO) {
        if (id == null) {
            throw new IllegalArgumentException("O ID da vaga não pode ser nulo.")
        }
        if (requestDTO == null) {
            throw new IllegalArgumentException("O objeto VagaRequestDTO não pode ser nulo.")
        }

        Vaga vagaExistente = vagaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Vaga não encontrada para o ID: ${id}"))

        Empresa novaEmpresa = null
        if (requestDTO.empresaId != null && (vagaExistente.empresa == null || vagaExistente.empresa.id != requestDTO.empresaId)) {
            novaEmpresa = empresaRepository.findById(requestDTO.empresaId)
                    .orElseThrow(() -> new NoSuchElementException("Empresa não encontrada para o ID: ${requestDTO.empresaId}"))
        }

        vagaExistente.atualizarDados(
                requestDTO.nome,
                requestDTO.descricao,
                requestDTO.local,
                requestDTO.nivel,
                requestDTO.competenciaIds,
                novaEmpresa
        )

        Vaga vagaAtualizada = vagaRepository.save(vagaExistente)
        return vagaMapper.toResponseDTO(vagaAtualizada)
    }
}
