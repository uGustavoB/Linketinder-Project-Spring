package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IDeletarEmpresaUseCase

@Service
class DeletarEmpresaUseCase implements IDeletarEmpresaUseCase {
    private final EmpresaRepository empresaRepository

    DeletarEmpresaUseCase(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository
    }

    @Override
    void executar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O ID da empresa não pode ser nulo.")
        }

        if (!empresaRepository.existsById(id)) {
            throw new NoSuchElementException("Empresa não encontrada.")
        }

        empresaRepository.deleteById(id)
    }
}
