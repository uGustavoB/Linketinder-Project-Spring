package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.request.EmpresaRequestDTO
import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO
import org.ugustavodev.msnucleo.mapper.EmpresaMapper
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IAtualizarEmpresaUseCase

@Service
class AtualizarEmpresaUseCase implements IAtualizarEmpresaUseCase {
    private final EmpresaRepository empresaRepository

    AtualizarEmpresaUseCase(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository
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

        empresaExistente.nome = requestDTO.nome
        empresaExistente.cnpj = requestDTO.cnpj
        empresaExistente.usuario.email = requestDTO.email
        empresaExistente.pais = requestDTO.pais
        empresaExistente.estado = requestDTO.estado
        empresaExistente.cep = requestDTO.cep
        empresaExistente.descricao = requestDTO.descricao
        if (requestDTO.senha) {
            empresaExistente.usuario.senha = requestDTO.senha
        }

        Empresa empresaAtualizada = empresaRepository.save(empresaExistente)
        return EmpresaMapper.toResponseDTO(empresaAtualizada)
    }
}
