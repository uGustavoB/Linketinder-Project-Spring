package org.ugustavodev.msnucleo.usecase.impl.empresas

import jakarta.transaction.Transactional
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.repositories.CurtidaRepository
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.repositories.VagaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IDeletarEmpresaUseCase

@Service
class DeletarEmpresaUseCase implements IDeletarEmpresaUseCase {
    private final EmpresaRepository empresaRepository
    private final CurtidaRepository curtidaRepository
    private final VagaRepository vagaRepository

    DeletarEmpresaUseCase(EmpresaRepository empresaRepository, CurtidaRepository curtidaRepository, VagaRepository vagaRepository) {
        this.empresaRepository = empresaRepository
        this.curtidaRepository = curtidaRepository
        this.vagaRepository = vagaRepository
    }

    @Override
    @Transactional
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

        curtidaRepository.deleteByVagaEmpresaId(id)
        vagaRepository.deleteByEmpresaId(id)
        empresaRepository.delete(empresa)
    }
}
