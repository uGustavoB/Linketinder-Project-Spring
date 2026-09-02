package org.ugustavodev.msnucleo.usecase.interfaces

import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO

interface IListarEmpresasUseCase {
    List<EmpresaResponseDTO> executar()
}
