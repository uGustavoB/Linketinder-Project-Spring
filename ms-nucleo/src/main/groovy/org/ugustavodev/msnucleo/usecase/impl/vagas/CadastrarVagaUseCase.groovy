package org.ugustavodev.msnucleo.usecase.impl.vagas

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.request.VagaRequestDTO
import org.ugustavodev.msnucleo.dto.response.VagaResponseDTO
import org.ugustavodev.msnucleo.mapper.VagaMapper
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.repositories.VagaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.vagas.ICadastrarVagaUseCase

@Service
class CadastrarVagaUseCase implements ICadastrarVagaUseCase {
    private final VagaRepository vagaRepository
    private final EmpresaRepository empresaRepository
    private final VagaMapper vagaMapper

    CadastrarVagaUseCase(VagaRepository vagaRepository, EmpresaRepository empresaRepository, VagaMapper vagaMapper) {
        this.vagaRepository = vagaRepository
        this.empresaRepository = empresaRepository
        this.vagaMapper = vagaMapper
    }

    @Override
    VagaResponseDTO executar(VagaRequestDTO requestDTO, Long empresaId) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("O objeto VagaRequestDTO não pode ser nulo.")
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("O ID da empresa não pode ser nulo.")
        }

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new NoSuchElementException("Empresa não encontrada para o ID: ${empresaId}"))

        Vaga vaga = vagaMapper.fromRequestDTOToEntity(requestDTO, empresa)
        Vaga savedVaga = vagaRepository.save(vaga)
        return vagaMapper.toResponseDTO(savedVaga)
    }
}
