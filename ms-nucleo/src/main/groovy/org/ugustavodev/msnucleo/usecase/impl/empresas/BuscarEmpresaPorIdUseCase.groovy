package org.ugustavodev.msnucleo.usecase.impl.empresas

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO
import org.ugustavodev.msnucleo.mapper.EmpresaMapper
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.empresas.IBuscarEmpresaPorIdUseCase

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role

@Service
class BuscarEmpresaPorIdUseCase implements IBuscarEmpresaPorIdUseCase {
    private final EmpresaRepository empresaRepository
    private final EmpresaMapper empresaMapper

    BuscarEmpresaPorIdUseCase(EmpresaRepository empresaRepository, EmpresaMapper empresaMapper) {
        this.empresaRepository = empresaRepository
        this.empresaMapper = empresaMapper
    }


    @Override
    EmpresaResponseDTO executar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O ID da empresa não pode ser nulo.")
        }

        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Empresa não encontrada."))

        Authentication auth = SecurityContextHolder.getContext().getAuthentication()
        if (auth != null && auth.getPrincipal() instanceof Usuario) {
            Usuario usuarioLogado = (Usuario) auth.getPrincipal()
            if (usuarioLogado.role == Role.CANDIDATO) {
                throw new AccessDeniedException("Candidatos não têm permissão para visualizar os dados sensíveis das empresas antes do Match.")
            }
            if (usuarioLogado.role == Role.EMPRESA && empresa.usuario.email != usuarioLogado.email) {
                throw new AccessDeniedException("Você não tem permissão para visualizar o perfil completo de outra empresa.")
            }
        }

        return empresaMapper.toResponseDTO(empresa)
    }
}
