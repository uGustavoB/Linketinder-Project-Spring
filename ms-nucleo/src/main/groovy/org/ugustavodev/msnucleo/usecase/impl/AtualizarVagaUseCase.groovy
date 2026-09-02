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

    AtualizarVagaUseCase(VagaRepository vagaRepository, EmpresaRepository empresaRepository) {
        this.vagaRepository = vagaRepository
        this.empresaRepository = empresaRepository
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

        if (requestDTO.empresaId != null && (vagaExistente.empresa == null || vagaExistente.empresa.id != requestDTO.empresaId)) {
            Empresa empresa = empresaRepository.findById(requestDTO.empresaId)
                    .orElseThrow(() -> new NoSuchElementException("Empresa não encontrada para o ID: ${requestDTO.empresaId}"))
            vagaExistente.empresa = empresa
        }

        vagaExistente.nome = requestDTO.nome
        vagaExistente.descricao = requestDTO.descricao
        vagaExistente.local = requestDTO.local
        vagaExistente.nivel = requestDTO.nivel
        if (requestDTO.competenciaIds != null) {
            vagaExistente.competenciaIds = new ArrayList<>(requestDTO.competenciaIds)
        }

        Vaga vagaAtualizada = vagaRepository.save(vagaExistente)
        return VagaMapper.toResponseDTO(vagaAtualizada)
    }
}
