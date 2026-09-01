package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.request.CandidatoRequestDTO
import org.ugustavodev.msnucleo.dto.response.CandidatoResponseDTO
import org.ugustavodev.msnucleo.mapper.CandidatoMapper
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IAtualizarCandidatoUseCase

@Service
class AtualizarCandidatoUseCase implements IAtualizarCandidatoUseCase {
    private final CandidatoRepository candidatoRepository

    AtualizarCandidatoUseCase(CandidatoRepository candidatoRepository) {
        this.candidatoRepository = candidatoRepository
    }

    @Override
    CandidatoResponseDTO executar(Long id, CandidatoRequestDTO requestDTO) {
        if (id == null) {
            throw new IllegalArgumentException("O ID do candidato não pode ser nulo.")
        }
        if (requestDTO == null) {
            throw new IllegalArgumentException("O objeto CandidatoRequestDTO não pode ser nulo.")
        }

        Candidato candidatoExistente = candidatoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Candidato não encontrado."))

        if (candidatoExistente.email != requestDTO.email) {
            Optional<Candidato> comMesmoEmail = candidatoRepository.findByEmail(requestDTO.email)
            if (comMesmoEmail.isPresent() && comMesmoEmail.get().id != id) {
                throw new IllegalArgumentException("Já existe outro candidato cadastrado com o email informado")
            }
        }

        if (candidatoExistente.cpf != requestDTO.cpf) {
            Optional<Candidato> comMesmoCpf = candidatoRepository.findByCpf(requestDTO.cpf)
            if (comMesmoCpf.isPresent() && comMesmoCpf.get().id != id) {
                throw new IllegalArgumentException("Já existe outro candidato cadastrado com o CPF informado")
            }
        }

        candidatoExistente.nome = requestDTO.nome
        candidatoExistente.sobrenome = requestDTO.sobrenome
        candidatoExistente.dataNascimento = requestDTO.dataNascimento?.toString()
        candidatoExistente.email = requestDTO.email
        candidatoExistente.cpf = requestDTO.cpf
        candidatoExistente.pais = requestDTO.pais
        candidatoExistente.estado = requestDTO.estado
        candidatoExistente.cep = requestDTO.cep
        candidatoExistente.descricao = requestDTO.descricao
        if (requestDTO.senha) {
            candidatoExistente.senha = requestDTO.senha
        }

        Candidato candidatoAtualizado = candidatoRepository.save(candidatoExistente)
        return CandidatoMapper.toResponseDTO(candidatoAtualizado)
    }
}
