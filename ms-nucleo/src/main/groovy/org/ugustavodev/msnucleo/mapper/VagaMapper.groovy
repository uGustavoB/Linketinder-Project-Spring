package org.ugustavodev.msnucleo.mapper

import org.springframework.stereotype.Component
import org.ugustavodev.msnucleo.dto.request.VagaRequestDTO
import org.ugustavodev.msnucleo.dto.response.VagaResponseDTO
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.model.Vaga

@Component
class VagaMapper {

    VagaResponseDTO toResponseDTO(Vaga vaga) {
        return new VagaResponseDTO(
                vaga.id,
                vaga.nome,
                vaga.descricao,
                vaga.local,
                vaga.nivel,
                vaga.empresa?.id,
                vaga.empresa?.nome,
                vaga.competenciaIds != null ? new ArrayList<>(vaga.competenciaIds) : []
        )
    }

    Vaga fromRequestDTOToEntity(VagaRequestDTO dto, Empresa empresa) {
        Vaga vaga = new Vaga()
        vaga.nome = dto.nome
        vaga.descricao = dto.descricao
        vaga.local = dto.local
        vaga.nivel = dto.nivel
        vaga.empresa = empresa
        vaga.competenciaIds = dto.competenciaIds != null ? new ArrayList<>(dto.competenciaIds) : []
        return vaga
    }
}
