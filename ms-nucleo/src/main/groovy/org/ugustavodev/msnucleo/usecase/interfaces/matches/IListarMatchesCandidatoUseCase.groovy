package org.ugustavodev.msnucleo.usecase.interfaces.matches

import org.ugustavodev.msnucleo.dto.response.MatchCandidatoResponseDTO

interface IListarMatchesCandidatoUseCase {
    List<MatchCandidatoResponseDTO> executar(Long idCandidato)
}
