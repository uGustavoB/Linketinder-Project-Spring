package org.ugustavodev.msnucleo.usecase.interfaces.curtidas

import org.ugustavodev.msnucleo.dto.response.CurtidaResponseDTO

interface ICandidatoCurteVagaUseCase {

    CurtidaResponseDTO executar(Long idCandidato, Long idEmpresa)

}
