package org.ugustavodev.msnucleo.usecase.interfaces.matches

import org.ugustavodev.msnucleo.dto.response.CandidatoPublicoResponseDTO

interface IListarCandidatosInteressadosUseCase {

    List<CandidatoPublicoResponseDTO> executar(Long vagaId, Long empresaId)

}
