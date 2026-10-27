package org.ugustavodev.msnucleo.usecase.impl.empresas

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.request.EmpresaRequestDTO
import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO
import org.ugustavodev.msnucleo.mapper.EmpresaMapper
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.empresas.IAtualizarEmpresaUseCase

import org.springframework.security.crypto.password.PasswordEncoder

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role

@Service
class AtualizarEmpresaUseCase implements IAtualizarEmpresaUseCase {
    private final EmpresaRepository empresaRepository
    private final PasswordEncoder passwordEncoder
    private final EmpresaMapper empresaMapper

    AtualizarEmpresaUseCase(EmpresaRepository empresaRepository, PasswordEncoder passwordEncoder, EmpresaMapper empresaMapper) {
        this.empresaRepository = empresaRepository
        this.passwordEncoder = passwordEncoder
        this.empresaMapper = empresaMapper
    }

    @Override
    @Transactional
    EmpresaResponseDTO executar(Long id, EmpresaRequestDTO requestDTO) {
        if (id == null) {
            throw new IllegalArgumentException("O ID da empresa não pode ser nulo.")
        }
        if (requestDTO == null) {
            throw new IllegalArgumentException("O objeto EmpresaRequestDTO não pode ser nulo.")
        }

        Empresa empresaExistente = empresaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Empresa não encontrada."))

        Authentication auth = SecurityContextHolder.getContext().getAuthentication()
        if (auth != null && auth.getPrincipal() instanceof Usuario) {
            Usuario usuarioLogado = (Usuario) auth.getPrincipal()
            if (usuarioLogado.role == Role.CANDIDATO) {
                throw new AccessDeniedException("Candidatos não têm permissão para atualizar dados de empresas.")
            }
            if (usuarioLogado.role == Role.EMPRESA && empresaExistente.usuario.email != usuarioLogado.email) {
                throw new AccessDeniedException("Você não tem permissão para modificar o perfil de outra empresa.")
            }
        }

        if (empresaExistente.usuario.email != requestDTO.email) {
            Optional<Empresa> comMesmoEmail = empresaRepository.findByUsuarioEmail(requestDTO.email)
            if (comMesmoEmail.isPresent() && comMesmoEmail.get().id != id) {
                throw new IllegalArgumentException("Já existe outra empresa cadastrada com o email informado")
            }
        }

        if (empresaExistente.cnpj != requestDTO.cnpj) {
            Optional<Empresa> comMesmoCnpj = empresaRepository.findByCnpj(requestDTO.cnpj)
            if (comMesmoCnpj.isPresent() && comMesmoCnpj.get().id != id) {
                throw new IllegalArgumentException("Já existe outra empresa cadastrada com o CNPJ informado")
            }
        }

        if (requestDTO.senha != null && requestDTO.senha.length() < 6) {
            throw new IllegalArgumentException("A senha deve ter no mínimo 6 caracteres.")
        }

        String senhaCriptografada = requestDTO.senha ? passwordEncoder.encode(requestDTO.senha) : null

        empresaExistente.atualizarDados(
                requestDTO.nome,
                requestDTO.cnpj,
                requestDTO.email,
                requestDTO.pais,
                requestDTO.estado,
                requestDTO.cep,
                requestDTO.descricao,
                senhaCriptografada
        )

        Empresa empresaAtualizada = empresaRepository.save(empresaExistente)
        return empresaMapper.toResponseDTO(empresaAtualizada)
    }
}

