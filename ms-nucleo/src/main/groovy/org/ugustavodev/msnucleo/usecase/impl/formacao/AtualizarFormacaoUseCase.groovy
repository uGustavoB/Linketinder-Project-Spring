package org.ugustavodev.msnucleo.usecase.impl.formacao

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.request.FormacaoRequestDTO
import org.ugustavodev.msnucleo.dto.response.FormacaoResponseDTO
import org.ugustavodev.msnucleo.mapper.FormacaoMapper
import org.ugustavodev.msnucleo.model.Formacao
import org.ugustavodev.msnucleo.repositories.FormacaoRepository
import org.ugustavodev.msnucleo.usecase.interfaces.formacao.IAtualizarFormacaoUseCase

@Service
class AtualizarFormacaoUseCase implements IAtualizarFormacaoUseCase {
    private final FormacaoRepository formacaoRepository
    private final FormacaoMapper formacaoMapper

    AtualizarFormacaoUseCase(FormacaoRepository formacaoRepository, FormacaoMapper formacaoMapper) {
        this.formacaoRepository = formacaoRepository
        this.formacaoMapper = formacaoMapper
    }

    @Override
    FormacaoResponseDTO executar(Long id, FormacaoRequestDTO requestDTO, Long candidatoId) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("O objeto FormacaoRequestDTO não pode ser nulo.")
        }

        Formacao formacaoExistente = formacaoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Formação não encontrada para o ID: ${id}"))

        if (formacaoExistente.candidato == null || formacaoExistente.candidato.id != candidatoId) {
            throw new IllegalArgumentException("Você não tem permissão para editar esta formação.")
        }

        formacaoExistente.instituicao = requestDTO.instituicao
        formacaoExistente.curso = requestDTO.curso
        formacaoExistente.nivel = requestDTO.nivel
        formacaoExistente.status = requestDTO.status
        formacaoExistente.dataInicio = requestDTO.dataInicio
        formacaoExistente.dataConclusao = requestDTO.dataConclusao

        Formacao formacaoAtualizada = formacaoRepository.save(formacaoExistente)
        return formacaoMapper.toResponseDTO(formacaoAtualizada)
    }
}
