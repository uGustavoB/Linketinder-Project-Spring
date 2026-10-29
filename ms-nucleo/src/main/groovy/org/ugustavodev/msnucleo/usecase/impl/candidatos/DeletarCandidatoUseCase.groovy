package org.ugustavodev.msnucleo.usecase.impl.candidatos

import jakarta.transaction.Transactional
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.repositories.CurtidaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.candidatos.IDeletarCandidatoUseCase

@Service
class DeletarCandidatoUseCase implements IDeletarCandidatoUseCase {

    private final CandidatoRepository candidatoRepository
    private final CurtidaRepository curtidaRepository

    DeletarCandidatoUseCase(CandidatoRepository candidatoRepository, CurtidaRepository curtidaRepository) {
        this.candidatoRepository = candidatoRepository
        this.curtidaRepository = curtidaRepository
    }

    @Override
    @Transactional
    void executar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O ID do candidato não pode ser nulo.")
        }

        Candidato candidato = candidatoRepository.findById(id)
                .orElseThrow { new NoSuchElementException("Candidato não encontrado.") }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication()
        if (auth != null && auth.getPrincipal() instanceof Usuario) {
            Usuario usuarioLogado = (Usuario) auth.getPrincipal()
            if (usuarioLogado.role == Role.EMPRESA) {
                throw new AccessDeniedException("Empresas não têm permissão para deletar candidatos.")
            }
            if (usuarioLogado.role == Role.CANDIDATO && candidato.usuario.email != usuarioLogado.email) {
                throw new AccessDeniedException("Você não tem permissão para deletar a conta de outro candidato.")
            }
        }

        curtidaRepository.deleteByCandidatoId(id)
        candidatoRepository.delete(candidato)
    }

}
