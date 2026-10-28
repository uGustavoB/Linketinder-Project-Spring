package org.ugustavodev.msnucleo.usecase.interfaces.candidatos

import org.ugustavodev.msnucleo.dto.request.CandidatoRequestDTO
import org.ugustavodev.msnucleo.dto.response.CandidatoResponseDTO

interface ICadastrarCandidatoUseCase {

    CandidatoResponseDTO executar(CandidatoRequestDTO requestDTO)

}
