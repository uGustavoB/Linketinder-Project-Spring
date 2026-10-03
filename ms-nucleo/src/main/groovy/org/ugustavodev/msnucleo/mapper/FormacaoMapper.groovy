package org.ugustavodev.msnucleo.mapper

import org.springframework.stereotype.Component
import org.ugustavodev.msnucleo.dto.request.FormacaoRequestDTO
import org.ugustavodev.msnucleo.dto.response.FormacaoResponseDTO
import org.ugustavodev.msnucleo.model.Formacao
import org.ugustavodev.msnucleo.model.Candidato

@Component
class FormacaoMapper {

    FormacaoResponseDTO toResponseDTO(Formacao formacao) {
        return new FormacaoResponseDTO(
                formacao.id,
                formacao.instituicao,
                formacao.curso,
                formacao.nivel,
                formacao.status,
                formacao.dataInicio,
                formacao.dataConclusao
        )
    }

    Formacao fromRequestDTOToEntity(FormacaoRequestDTO dto, Candidato candidato) {
        Formacao formacao = new Formacao()
        formacao.instituicao = dto.instituicao
        formacao.curso = dto.curso
        formacao.nivel = dto.nivel
        formacao.status = dto.status
        formacao.dataInicio = dto.dataInicio
        formacao.dataConclusao = dto.dataConclusao
        formacao.candidato = candidato
        return formacao
    }
}
