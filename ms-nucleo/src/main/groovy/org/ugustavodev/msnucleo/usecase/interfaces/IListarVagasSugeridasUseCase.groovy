package org.ugustavodev.msnucleo.usecase.interfaces

import org.ugustavodev.msnucleo.dto.response.VagaPublicoResponseDTO

interface IListarVagasSugeridasUseCase {
    List<VagaPublicoResponseDTO> executar(Long candidatoId)
}
