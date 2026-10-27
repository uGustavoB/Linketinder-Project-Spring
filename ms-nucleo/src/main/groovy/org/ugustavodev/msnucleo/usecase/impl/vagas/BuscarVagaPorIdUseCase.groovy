package org.ugustavodev.msnucleo.usecase.impl.vagas

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.dto.response.VagaResponseDTO
import org.ugustavodev.msnucleo.mapper.VagaMapper
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.repositories.CurtidaRepository
import org.ugustavodev.msnucleo.repositories.VagaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IBuscarVagaPorIdUseCase

@Service
class BuscarVagaPorIdUseCase implements IBuscarVagaPorIdUseCase {

    private final VagaRepository vagaRepository
    private final VagaMapper vagaMapper
    private final CurtidaRepository curtidaRepository
    private final CandidatoRepository candidatoRepository

    BuscarVagaPorIdUseCase(
            VagaRepository vagaRepository,
            VagaMapper vagaMapper,
            CurtidaRepository curtidaRepository,
            CandidatoRepository candidatoRepository
    ) {
        this.vagaRepository = vagaRepository
        this.vagaMapper = vagaMapper
        this.curtidaRepository = curtidaRepository
        this.candidatoRepository = candidatoRepository
    }

    @Override
    VagaResponseDTO executar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O ID da vaga não pode ser nulo.")
        }

        Vaga vaga = vagaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Vaga não encontrada para o ID: ${id}"))

        Authentication auth = SecurityContextHolder.getContext().getAuthentication()
        if (auth != null && auth.getPrincipal() instanceof Usuario) {
            Usuario usuario = (Usuario) auth.getPrincipal()

            if (usuario.role == Role.EMPRESA && vaga.empresa.usuario?.email == usuario.email) {
                return vagaMapper.toResponseDTO(vaga)
            }

            if (usuario.role == Role.CANDIDATO) {
                Optional<Candidato> candidato = candidatoRepository.findByUsuarioEmail(usuario.email)

                if (candidato.isPresent() && curtidaRepository.existsMatchNaVaga(candidato.get().id, vaga.id)) {
                    return vagaMapper.toResponseDTO(vaga)
                }
            }
        }


        return new VagaResponseDTO(
                vaga.id,
                vaga.nome,
                vaga.descricao,
                vaga.local,
                vaga.nivel,
                null,
                null
        )
    }

}
