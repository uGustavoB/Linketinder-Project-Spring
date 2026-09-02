package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO
import org.ugustavodev.msnucleo.mapper.EmpresaMapper
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IListarEmpresasUseCase

@Service
class ListarEmpresasUseCase implements IListarEmpresasUseCase {
    private final EmpresaRepository empresaRepository

    ListarEmpresasUseCase(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository
    }

    @Override
    List<EmpresaResponseDTO> executar() {
        return empresaRepository.findAll()
                .collect { EmpresaMapper.toResponseDTO(it) }
    }
}
