package org.ugustavodev.msnucleo.usecase.interfaces

import org.ugustavodev.msnucleo.dto.request.FormacaoRequestDTO
import org.ugustavodev.msnucleo.dto.response.FormacaoResponseDTO

interface IAtualizarFormacaoUseCase {
    FormacaoResponseDTO executar(Long id, FormacaoRequestDTO requestDTO, Long candidatoId)
}
