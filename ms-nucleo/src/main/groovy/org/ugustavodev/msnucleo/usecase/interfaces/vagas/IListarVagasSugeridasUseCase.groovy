package org.ugustavodev.msnucleo.usecase.interfaces.vagas

import org.ugustavodev.msnucleo.dto.response.VagaPublicoResponseDTO

interface IListarVagasSugeridasUseCase {

    List<VagaPublicoResponseDTO> executar(Long candidatoId)

}
