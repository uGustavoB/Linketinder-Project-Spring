package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.response.CandidatoPublicoResponseDTO
import org.ugustavodev.msnucleo.mapper.CandidatoMapper
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IListarCandidatosUseCase

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role

@Service
class ListarCandidatosUseCase implements IListarCandidatosUseCase {
    private final CandidatoRepository candidatoRepository
    private final CandidatoMapper candidatoMapper

    ListarCandidatosUseCase(CandidatoRepository candidatoRepository, CandidatoMapper candidatoMapper) {
        this.candidatoRepository = candidatoRepository
        this.candidatoMapper = candidatoMapper
    }

    @Override
    List<CandidatoPublicoResponseDTO> executar() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication()
        if (auth != null && auth.getPrincipal() instanceof Usuario) {
            Usuario usuarioLogado = (Usuario) auth.getPrincipal()
            if (usuarioLogado.role == Role.CANDIDATO) {
                return candidatoRepository.findByUsuarioEmail(usuarioLogado.email)
                        .map(c -> List.of(candidatoMapper.toPublicoResponseDTO(c)))
                        .orElse(Collections.emptyList())
            }
        }

        return candidatoRepository.findAll()
                .collect { candidatoMapper.toPublicoResponseDTO(it) }
    }
}
