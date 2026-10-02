package org.ugustavodev.msnucleo.usecase.interfaces

import org.ugustavodev.msnucleo.dto.response.CandidatoPublicoResponseDTO

interface IListarCandidatosSugeridosUseCase {
    List<CandidatoPublicoResponseDTO> executar(Long vagaId, Long empresaId)
}
