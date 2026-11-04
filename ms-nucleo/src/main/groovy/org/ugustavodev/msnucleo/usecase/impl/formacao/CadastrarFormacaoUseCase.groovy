package org.ugustavodev.msnucleo.usecase.impl.formacao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.ugustavodev.msnucleo.dto.request.FormacaoRequestDTO
import org.ugustavodev.msnucleo.dto.response.FormacaoResponseDTO
import org.ugustavodev.msnucleo.mapper.FormacaoMapper
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.model.Formacao
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.repositories.FormacaoRepository
import org.ugustavodev.msnucleo.usecase.interfaces.formacao.ICadastrarFormacaoUseCase

@Service
class CadastrarFormacaoUseCase implements ICadastrarFormacaoUseCase {

    private final FormacaoRepository formacaoRepository
    private final CandidatoRepository candidatoRepository
    private final FormacaoMapper formacaoMapper

    CadastrarFormacaoUseCase(FormacaoRepository formacaoRepository, CandidatoRepository candidatoRepository, FormacaoMapper formacaoMapper) {
        this.formacaoRepository = formacaoRepository
        this.candidatoRepository = candidatoRepository
        this.formacaoMapper = formacaoMapper
    }

    @Override
    @Transactional
    FormacaoResponseDTO executar(FormacaoRequestDTO requestDTO, Long candidatoId) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("O objeto FormacaoRequestDTO não pode ser nulo.")
        }
        if (candidatoId == null) {
            throw new IllegalArgumentException("O ID do candidato não pode ser nulo.")
        }

        Candidato candidato = candidatoRepository.findById(candidatoId)
                .orElseThrow { new NoSuchElementException("Candidato não encontrado para o ID: ${candidatoId}") }

        Formacao formacao = formacaoMapper.fromRequestDTOToEntity(requestDTO, candidato)
        Formacao savedFormacao = formacaoRepository.save(formacao)

        return formacaoMapper.toResponseDTO(savedFormacao)
    }

}
