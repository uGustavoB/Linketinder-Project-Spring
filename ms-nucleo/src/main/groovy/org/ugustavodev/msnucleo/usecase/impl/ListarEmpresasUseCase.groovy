package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO
import org.ugustavodev.msnucleo.dto.response.EmpresaPublicoResponseDTO
import org.ugustavodev.msnucleo.mapper.EmpresaMapper
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IListarEmpresasUseCase

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role

@Service
class ListarEmpresasUseCase implements IListarEmpresasUseCase {
    private final EmpresaRepository empresaRepository
    private final EmpresaMapper empresaMapper

    ListarEmpresasUseCase(EmpresaRepository empresaRepository, EmpresaMapper empresaMapper) {
        this.empresaRepository = empresaRepository
        this.empresaMapper = empresaMapper
    }

    @Override
    List<EmpresaPublicoResponseDTO> executar() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication()
        if (auth != null && auth.getPrincipal() instanceof Usuario) {
            Usuario usuarioLogado = (Usuario) auth.getPrincipal()
            if (usuarioLogado.role == Role.EMPRESA) {
                return empresaRepository.findByUsuarioEmail(usuarioLogado.email)
                        .map(e -> List.of(empresaMapper.toPublicoResponseDTO(e)))
                        .orElse(Collections.emptyList())
            }
        }

        return empresaRepository.findAll()
                .collect { empresaMapper.toPublicoResponseDTO(it) }
    }
}
