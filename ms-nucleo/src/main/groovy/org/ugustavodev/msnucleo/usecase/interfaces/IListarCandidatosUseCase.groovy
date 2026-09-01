package org.ugustavodev.msnucleo.usecase.interfaces

import org.ugustavodev.msnucleo.dto.response.CandidatoResponseDTO

interface IListarCandidatosUseCase {
    List<CandidatoResponseDTO> executar()
}
