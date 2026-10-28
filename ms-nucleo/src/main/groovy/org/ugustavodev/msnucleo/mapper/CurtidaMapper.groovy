package org.ugustavodev.msnucleo.mapper

import org.springframework.stereotype.Component
import org.ugustavodev.msnucleo.model.Curtida
import org.ugustavodev.msnucleo.dto.response.CurtidaResponseDTO

@Component
class CurtidaMapper {

    CurtidaResponseDTO toResponseDTO(Curtida curtida) {
        if (curtida == null) return null

        return new CurtidaResponseDTO(
                idCurtida: curtida.id,
                candidatoId: curtida.candidato?.id,
                vagaId: curtida.vaga?.id,
                candidatoCurtiuEm: curtida.candidatoCurtiuEm,
                empresaCurtiuEm: curtida.empresaCurtiuEm,
                match: curtida.isMatch()
        )
    }

    List<CurtidaResponseDTO> toResponseDTOList(List<Curtida> curtidas) {
        if (curtidas == null) return []
        return curtidas.collect { toResponseDTO(it) }
    }

}
