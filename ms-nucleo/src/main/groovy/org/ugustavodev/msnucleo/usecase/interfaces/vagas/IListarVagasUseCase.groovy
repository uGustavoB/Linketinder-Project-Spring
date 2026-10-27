package org.ugustavodev.msnucleo.usecase.interfaces.vagas


import org.ugustavodev.msnucleo.dto.response.VagaPublicoResponseDTO

interface IListarVagasUseCase {
    List<VagaPublicoResponseDTO> executar(Long empresaId)
}
