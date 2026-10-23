package org.ugustavodev.msnucleo.usecase.impl

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.request.CandidatoRequestDTO
import org.ugustavodev.msnucleo.dto.response.CandidatoResponseDTO
import org.ugustavodev.msnucleo.mapper.CandidatoMapper
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IAtualizarCandidatoUseCase

import org.springframework.security.crypto.password.PasswordEncoder

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role

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
    @Transactional
    CandidatoResponseDTO executar(Long id, CandidatoRequestDTO requestDTO) {
        if (id == null) {
            throw new IllegalArgumentException("O ID do candidato não pode ser nulo.")
        }
        if (requestDTO == null) {
            throw new IllegalArgumentException("O objeto CandidatoRequestDTO não pode ser nulo.")
        }

        Candidato candidatoExistente = candidatoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Candidato não encontrado."))

        Authentication auth = SecurityContextHolder.getContext().getAuthentication()
        if (auth != null && auth.getPrincipal() instanceof Usuario) {
            Usuario usuarioLogado = (Usuario) auth.getPrincipal()
            if (usuarioLogado.role == Role.EMPRESA) {
                throw new AccessDeniedException("Empresas não têm permissão para atualizar o perfil de candidatos.")
            }
            if (usuarioLogado.role == Role.CANDIDATO && candidatoExistente.usuario.email != usuarioLogado.email) {
                throw new AccessDeniedException("Você não tem permissão para modificar o perfil de outro candidato.")
            }
        }

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

        if (requestDTO.senha != null && requestDTO.senha.length() < 6) {
            throw new IllegalArgumentException("A senha deve ter no mínimo 6 caracteres.")
        }

        String senhaCriptografada = requestDTO.senha ? passwordEncoder.encode(requestDTO.senha) : null
        
        candidatoExistente.atualizarDados(
                requestDTO.nome,
                requestDTO.sobrenome,
                requestDTO.dataNascimento,
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

