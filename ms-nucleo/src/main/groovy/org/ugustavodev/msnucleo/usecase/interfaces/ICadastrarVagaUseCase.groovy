package org.ugustavodev.msnucleo.usecase.interfaces

import org.ugustavodev.msnucleo.dto.request.VagaRequestDTO
import org.ugustavodev.msnucleo.dto.response.VagaResponseDTO

interface ICadastrarVagaUseCase {
    VagaResponseDTO executar(VagaRequestDTO requestDTO, Long empresaId)
}
