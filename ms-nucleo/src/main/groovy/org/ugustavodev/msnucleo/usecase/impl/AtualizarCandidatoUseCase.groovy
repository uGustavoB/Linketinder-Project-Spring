package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.request.CandidatoRequestDTO
import org.ugustavodev.msnucleo.dto.response.CandidatoResponseDTO
import org.ugustavodev.msnucleo.mapper.CandidatoMapper
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IAtualizarCandidatoUseCase

import org.springframework.security.crypto.password.PasswordEncoder

@Service
class AtualizarCandidatoUseCase implements IAtualizarCandidatoUseCase {
    private final CandidatoRepository candidatoRepository
    private final PasswordEncoder passwordEncoder
    private final CandidatoMapper candidatoMapper

    AtualizarCandidatoUseCase(CandidatoRepository candidatoRepository, PasswordEncoder passwordEncoder, CandidatoMapper candidatoMapper) {
        this.candidatoRepository = candidatoRepository
        this.passwordEncoder = passwordEncoder
        this.candidatoMapper = candidatoMapper
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

        if (candidatoExistente.usuario.email != requestDTO.email) {
            Optional<Candidato> comMesmoEmail = candidatoRepository.findByUsuarioEmail(requestDTO.email)
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

        String senhaCriptografada = requestDTO.senha ? passwordEncoder.encode(requestDTO.senha) : null
        
        candidatoExistente.atualizarDados(
                requestDTO.nome,
                requestDTO.sobrenome,
                requestDTO.dataNascimento?.toString(),
                requestDTO.email,
                requestDTO.cpf,
                requestDTO.pais,
                requestDTO.estado,
                requestDTO.cep,
                requestDTO.descricao,
                senhaCriptografada
        )

        Candidato candidatoAtualizado = candidatoRepository.save(candidatoExistente)
        return candidatoMapper.toResponseDTO(candidatoAtualizado)
    }
}

