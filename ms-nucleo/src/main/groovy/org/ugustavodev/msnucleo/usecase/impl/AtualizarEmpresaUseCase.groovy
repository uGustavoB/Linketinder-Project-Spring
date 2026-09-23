package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.request.EmpresaRequestDTO
import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO
import org.ugustavodev.msnucleo.mapper.EmpresaMapper
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IAtualizarEmpresaUseCase

import org.springframework.security.crypto.password.PasswordEncoder

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
    EmpresaResponseDTO executar(Long id, EmpresaRequestDTO requestDTO) {
        if (id == null) {
            throw new IllegalArgumentException("O ID da empresa não pode ser nulo.")
        }
        if (requestDTO == null) {
            throw new IllegalArgumentException("O objeto EmpresaRequestDTO não pode ser nulo.")
        }

        Empresa empresaExistente = empresaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Empresa não encontrada."))

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

