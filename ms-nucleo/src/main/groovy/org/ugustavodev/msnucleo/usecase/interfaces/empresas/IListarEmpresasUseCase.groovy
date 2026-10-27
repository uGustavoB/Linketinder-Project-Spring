package org.ugustavodev.msnucleo.usecase.interfaces.empresas


import org.ugustavodev.msnucleo.dto.response.EmpresaPublicoResponseDTO

interface IListarEmpresasUseCase {
    List<EmpresaPublicoResponseDTO> executar()
}
