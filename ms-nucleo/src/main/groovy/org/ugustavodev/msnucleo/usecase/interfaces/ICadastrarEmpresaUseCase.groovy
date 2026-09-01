package org.ugustavodev.msnucleo.usecase.interfaces

import org.ugustavodev.msnucleo.dto.request.EmpresaRequestDTO
import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO

interface ICadastrarEmpresaUseCase {
    EmpresaResponseDTO executar(EmpresaRequestDTO requestDTO)
}
