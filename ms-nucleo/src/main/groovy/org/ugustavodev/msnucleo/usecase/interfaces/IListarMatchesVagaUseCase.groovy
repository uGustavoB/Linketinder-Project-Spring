package org.ugustavodev.msnucleo.usecase.interfaces

import org.ugustavodev.msnucleo.dto.response.MatchVagaResponseDTO

interface IListarMatchesVagaUseCase {
    List<MatchVagaResponseDTO> executar(Long idVaga, Long idEmpresaLogada)
}
