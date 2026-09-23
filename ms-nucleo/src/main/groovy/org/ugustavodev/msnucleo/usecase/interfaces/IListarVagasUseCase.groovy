package org.ugustavodev.msnucleo.usecase.interfaces

import org.ugustavodev.msnucleo.dto.response.VagaResponseDTO
import org.ugustavodev.msnucleo.dto.response.VagaPublicoResponseDTO

interface IListarVagasUseCase {
    List<VagaPublicoResponseDTO> executar(Long empresaId)
}
