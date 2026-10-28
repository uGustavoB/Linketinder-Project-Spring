package org.ugustavodev.msnucleo.usecase.interfaces.vagas

import org.ugustavodev.msnucleo.dto.response.VagaResponseDTO

interface IBuscarVagaPorIdUseCase {

    VagaResponseDTO executar(Long id)

}
