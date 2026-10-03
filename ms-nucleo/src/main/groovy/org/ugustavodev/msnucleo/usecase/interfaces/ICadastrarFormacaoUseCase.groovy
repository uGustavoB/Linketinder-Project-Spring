package org.ugustavodev.msnucleo.usecase.interfaces

import org.ugustavodev.msnucleo.dto.request.FormacaoRequestDTO
import org.ugustavodev.msnucleo.dto.response.FormacaoResponseDTO

interface ICadastrarFormacaoUseCase {
    FormacaoResponseDTO executar(FormacaoRequestDTO requestDTO, Long candidatoId)
}
