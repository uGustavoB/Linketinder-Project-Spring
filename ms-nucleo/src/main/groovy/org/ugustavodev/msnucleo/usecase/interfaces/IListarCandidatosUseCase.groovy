package org.ugustavodev.msnucleo.usecase.interfaces

import org.ugustavodev.msnucleo.dto.response.CandidatoPublicoResponseDTO

interface IListarCandidatosUseCase {
    List<CandidatoPublicoResponseDTO> executar()
}
