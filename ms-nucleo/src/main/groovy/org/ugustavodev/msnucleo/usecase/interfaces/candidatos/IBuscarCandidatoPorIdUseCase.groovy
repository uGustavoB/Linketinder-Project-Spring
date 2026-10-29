package org.ugustavodev.msnucleo.usecase.interfaces.candidatos

import org.ugustavodev.msnucleo.dto.response.CandidatoResponseDTO

interface IBuscarCandidatoPorIdUseCase {

    CandidatoResponseDTO executar(Long id)

}
