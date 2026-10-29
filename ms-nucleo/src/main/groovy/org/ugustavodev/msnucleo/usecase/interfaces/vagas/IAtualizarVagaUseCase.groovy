package org.ugustavodev.msnucleo.usecase.interfaces.vagas

import org.ugustavodev.msnucleo.dto.request.VagaRequestDTO
import org.ugustavodev.msnucleo.dto.response.VagaResponseDTO

interface IAtualizarVagaUseCase {

    VagaResponseDTO executar(Long id, VagaRequestDTO requestDTO, Long empresaId)

}
