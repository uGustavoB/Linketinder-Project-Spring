package org.ugustavodev.msnucleo.usecase.interfaces

import org.ugustavodev.msnucleo.dto.response.CurtidaResponseDTO

interface ICandidatoCurteVagaUseCase {
    CurtidaResponseDTO executar(Long idCandidato, Long idEmpresa)
}
