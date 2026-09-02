package org.ugustavodev.msnucleo.usecase.interfaces

import org.ugustavodev.msnucleo.dto.response.VagaResponseDTO

interface IListarVagasUseCase {
    List<VagaResponseDTO> executar(Long empresaId)
}
