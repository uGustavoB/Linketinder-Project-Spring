package org.ugustavodev.msnucleo.usecase.interfaces

import org.ugustavodev.msnucleo.dto.response.CandidatoResponseDTO

interface IBuscarCandidatoPorIdUseCase {
    CandidatoResponseDTO executar(Long id)
}
