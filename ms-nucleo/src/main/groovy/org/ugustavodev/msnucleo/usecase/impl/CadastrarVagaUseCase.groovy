package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.request.VagaRequestDTO
import org.ugustavodev.msnucleo.dto.response.VagaResponseDTO
import org.ugustavodev.msnucleo.mapper.VagaMapper
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.repositories.VagaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.ICadastrarVagaUseCase

@Service
class CadastrarVagaUseCase implements ICadastrarVagaUseCase {
    private final VagaRepository vagaRepository
    private final EmpresaRepository empresaRepository

    CadastrarVagaUseCase(VagaRepository vagaRepository, EmpresaRepository empresaRepository) {
        this.vagaRepository = vagaRepository
        this.empresaRepository = empresaRepository
    }

    @Override
    VagaResponseDTO executar(VagaRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("O objeto VagaRequestDTO não pode ser nulo.")
        }

        Empresa empresa = empresaRepository.findById(requestDTO.empresaId)
                .orElseThrow(() -> new NoSuchElementException("Empresa não encontrada para o ID: ${requestDTO.empresaId}"))

        Vaga vaga = VagaMapper.fromRequestDTOToEntity(requestDTO, empresa)
        Vaga savedVaga = vagaRepository.save(vaga)
        return VagaMapper.toResponseDTO(savedVaga)
    }
}
