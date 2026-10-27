package org.ugustavodev.msnucleo.usecase.impl.candidatos

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.response.CandidatoResponseDTO
import org.ugustavodev.msnucleo.mapper.CandidatoMapper
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.usecase.interfaces.candidatos.IBuscarCandidatoPorIdUseCase

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role

@Service
class BuscarCandidatoPorIdUseCase implements IBuscarCandidatoPorIdUseCase {
    private final CandidatoRepository candidatoRepository
    private final CandidatoMapper candidatoMapper

    BuscarCandidatoPorIdUseCase(CandidatoRepository candidatoRepository, CandidatoMapper candidatoMapper) {
        this.candidatoRepository = candidatoRepository
        this.candidatoMapper = candidatoMapper
    }


    @Override
    CandidatoResponseDTO executar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O ID do candidato não pode ser nulo.")
        }

        Candidato candidato = candidatoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Candidato não encontrado."))

        Authentication auth = SecurityContextHolder.getContext().getAuthentication()
        if (auth != null && auth.getPrincipal() instanceof Usuario) {
            Usuario usuarioLogado = (Usuario) auth.getPrincipal()
            
            if (usuarioLogado.role == Role.EMPRESA) {
                throw new AccessDeniedException("Empresas não têm permissão para visualizar os dados sensíveis dos candidatos antes do Match.")
            }
            if (usuarioLogado.role == Role.CANDIDATO && candidato.usuario.email != usuarioLogado.email) {
                throw new AccessDeniedException("Você não tem permissão para visualizar o perfil de outro candidato.")
            }
        }

        return candidatoMapper.toResponseDTO(candidato)
    }
}
