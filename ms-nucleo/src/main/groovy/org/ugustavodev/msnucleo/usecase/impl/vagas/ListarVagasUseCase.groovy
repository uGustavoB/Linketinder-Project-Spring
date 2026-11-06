package org.ugustavodev.msnucleo.usecase.impl.vagas

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.dto.response.VagaPublicoResponseDTO
import org.ugustavodev.msnucleo.mapper.VagaMapper
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.repositories.VagaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.vagas.IListarVagasUseCase

@Service
class ListarVagasUseCase implements IListarVagasUseCase {

    private final VagaRepository vagaRepository
    private final VagaMapper vagaMapper
    private final EmpresaRepository empresaRepository

    ListarVagasUseCase(VagaRepository vagaRepository, VagaMapper vagaMapper, EmpresaRepository empresaRepository) {
        this.vagaRepository = vagaRepository
        this.vagaMapper = vagaMapper
        this.empresaRepository = empresaRepository
    }

    @Override
    List<VagaPublicoResponseDTO> executar(Long empresaId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication()
        Usuario usuarioLogado = (auth != null && auth.getPrincipal() instanceof Usuario) ? (Usuario) auth.getPrincipal() : null
        Empresa empresaLogada = null

        if (usuarioLogado != null) {
            if (usuarioLogado.role == Role.CANDIDATO && empresaId != null) {
                throw new AccessDeniedException("Candidatos não têm permissão para filtrar vagas por empresa.")
            }
            if (usuarioLogado.role == Role.EMPRESA) {
                empresaLogada = empresaRepository.findByUsuarioEmail(usuarioLogado.email).orElse(null)
                if (empresaId != null && empresaLogada != null && empresaLogada.id != empresaId) {
                    throw new AccessDeniedException("Você não tem permissão para visualizar as vagas de outra empresa.")
                }
            }
        }

        List<Vaga> vagas = empresaId != null ?
                vagaRepository.findByEmpresaId(empresaId) :
                vagaRepository.findAll()

        return vagas.collect { vaga ->
            boolean podeVerEmpresaId = empresaLogada != null && vaga.empresa?.id == empresaLogada.id
            vagaMapper.toPublicoResponseDTO(vaga, podeVerEmpresaId)
        }
    }

}
