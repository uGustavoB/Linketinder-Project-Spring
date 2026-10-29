package org.ugustavodev.msnucleo.usecase.interfaces.empresas

import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO

interface IBuscarEmpresaPorIdUseCase {

    EmpresaResponseDTO executar(Long id)

}
