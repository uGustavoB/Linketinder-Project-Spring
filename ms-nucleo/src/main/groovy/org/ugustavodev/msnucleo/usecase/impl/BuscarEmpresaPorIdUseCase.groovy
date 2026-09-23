package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO
import org.ugustavodev.msnucleo.mapper.EmpresaMapper
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IBuscarEmpresaPorIdUseCase

@Service
class BuscarEmpresaPorIdUseCase implements IBuscarEmpresaPorIdUseCase {
    private final EmpresaRepository empresaRepository
    private final EmpresaMapper empresaMapper

    BuscarEmpresaPorIdUseCase(EmpresaRepository empresaRepository, EmpresaMapper empresaMapper) {
        this.empresaRepository = empresaRepository
        this.empresaMapper = empresaMapper
    }

    @Override
    EmpresaResponseDTO executar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O ID da empresa não pode ser nulo.")
        }

        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Empresa não encontrada."))

        return empresaMapper.toResponseDTO(empresa)
    }
}
