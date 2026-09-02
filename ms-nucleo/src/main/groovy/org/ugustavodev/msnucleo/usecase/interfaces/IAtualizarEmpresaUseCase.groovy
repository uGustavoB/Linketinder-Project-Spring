package org.ugustavodev.msnucleo.usecase.interfaces

import org.ugustavodev.msnucleo.dto.request.EmpresaRequestDTO
import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO

interface IAtualizarEmpresaUseCase {
    EmpresaResponseDTO executar(Long id, EmpresaRequestDTO requestDTO)
}
