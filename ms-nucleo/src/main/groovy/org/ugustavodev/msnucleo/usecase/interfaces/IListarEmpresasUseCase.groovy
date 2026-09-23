package org.ugustavodev.msnucleo.usecase.interfaces

import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO
import org.ugustavodev.msnucleo.dto.response.EmpresaPublicoResponseDTO

interface IListarEmpresasUseCase {
    List<EmpresaPublicoResponseDTO> executar()
}
