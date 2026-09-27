package org.ugustavodev.msnucleo.usecase.interfaces

import org.ugustavodev.msnucleo.dto.response.CurtidaResponseDTO

interface IEmpresaCurteCandidatoUseCase {
    CurtidaResponseDTO executar(Long idEmpresa, Long idCandidato, Long idVaga)
}
