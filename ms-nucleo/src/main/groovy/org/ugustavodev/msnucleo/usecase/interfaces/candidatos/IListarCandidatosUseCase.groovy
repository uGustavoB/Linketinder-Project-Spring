package org.ugustavodev.msnucleo.usecase.interfaces.candidatos

import org.ugustavodev.msnucleo.dto.response.CandidatoPublicoResponseDTO

interface IListarCandidatosUseCase {
    List<CandidatoPublicoResponseDTO> executar()
}
