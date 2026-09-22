package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.request.EmpresaRequestDTO
import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO
import org.ugustavodev.msnucleo.mapper.EmpresaMapper
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.ICadastrarEmpresaUseCase

@Service
class CadastrarEmpresaUseCase implements ICadastrarEmpresaUseCase {
    private final EmpresaRepository empresaRepository

    CadastrarEmpresaUseCase(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository
    }

    @Override
    EmpresaResponseDTO executar(EmpresaRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("O objeto EmpresaRequestDTO não pode ser nulo.")
        }

        if (empresaRepository.findByUsuarioEmail(requestDTO.email).isPresent()) {
            throw new IllegalArgumentException("Já existe uma empresa cadastrada com o email informado")
        }

        if (empresaRepository.findByCnpj(requestDTO.cnpj).isPresent()) {
            throw new IllegalArgumentException("Já existe uma empresa cadastrada com o CNPJ informado")
        }

        Empresa empresa = EmpresaMapper.fromRequestDTOToEntity(requestDTO)
        Empresa savedEmpresa = empresaRepository.save(empresa)
        return EmpresaMapper.toResponseDTO(savedEmpresa)
    }
}
