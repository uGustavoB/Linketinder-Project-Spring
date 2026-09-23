package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO
import org.ugustavodev.msnucleo.dto.response.EmpresaPublicoResponseDTO
import org.ugustavodev.msnucleo.mapper.EmpresaMapper
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IListarEmpresasUseCase

@Service
class ListarEmpresasUseCase implements IListarEmpresasUseCase {
    private final EmpresaRepository empresaRepository
    private final EmpresaMapper empresaMapper

    ListarEmpresasUseCase(EmpresaRepository empresaRepository, EmpresaMapper empresaMapper) {
        this.empresaRepository = empresaRepository
        this.empresaMapper = empresaMapper
    }

    @Override
    List<EmpresaPublicoResponseDTO> executar() {
        return empresaRepository.findAll()
                .collect { empresaMapper.toPublicoResponseDTO(it) }
    }
}
