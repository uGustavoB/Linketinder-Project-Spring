package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IDeletarEmpresaUseCase

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.model.Empresa

@Service
class DeletarEmpresaUseCase implements IDeletarEmpresaUseCase {
    private final EmpresaRepository empresaRepository

    DeletarEmpresaUseCase(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository
    }

    @Override
    void executar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O ID da empresa não pode ser nulo.")
        }

        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Empresa não encontrada."))

        Authentication auth = SecurityContextHolder.getContext().getAuthentication()
        if (auth != null && auth.getPrincipal() instanceof Usuario) {
            Usuario usuarioLogado = (Usuario) auth.getPrincipal()
            if (usuarioLogado.role == Role.CANDIDATO) {
                throw new AccessDeniedException("Candidatos não têm permissão para deletar empresas.")
            }
            if (usuarioLogado.role == Role.EMPRESA && empresa.usuario.email != usuarioLogado.email) {
                throw new AccessDeniedException("Você não tem permissão para deletar a conta de outra empresa.")
            }
        }

        empresaRepository.delete(empresa)
    }
}
