package org.ugustavodev.msnucleo.usecase.interfaces

import org.ugustavodev.msnucleo.dto.response.MatchCandidatoResponseDTO

interface IListarMatchesCandidatoUseCase {
    List<MatchCandidatoResponseDTO> executar(Long idCandidato)
}
