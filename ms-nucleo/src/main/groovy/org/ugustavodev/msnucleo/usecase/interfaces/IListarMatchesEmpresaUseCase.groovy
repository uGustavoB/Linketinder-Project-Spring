package org.ugustavodev.msnucleo.usecase.interfaces

import org.ugustavodev.msnucleo.dto.response.MatchEmpresaResponseDTO

interface IListarMatchesEmpresaUseCase {
    List<MatchEmpresaResponseDTO> executar(Long idEmpresa)
}
