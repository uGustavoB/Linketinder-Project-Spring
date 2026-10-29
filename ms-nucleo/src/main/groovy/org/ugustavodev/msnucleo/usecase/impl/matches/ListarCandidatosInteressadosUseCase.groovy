package org.ugustavodev.msnucleo.usecase.impl.matches

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.response.CandidatoPublicoResponseDTO
import org.ugustavodev.msnucleo.mapper.CandidatoMapper
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.repositories.VagaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.matches.IListarCandidatosInteressadosUseCase

@Service
class ListarCandidatosInteressadosUseCase implements IListarCandidatosInteressadosUseCase {

    private final CandidatoRepository candidatoRepository
    private final VagaRepository vagaRepository
    private final CandidatoMapper candidatoMapper

    ListarCandidatosInteressadosUseCase(CandidatoRepository candidatoRepository, VagaRepository vagaRepository, CandidatoMapper candidatoMapper) {
        this.candidatoRepository = candidatoRepository
        this.vagaRepository = vagaRepository
        this.candidatoMapper = candidatoMapper
    }

    @Override
    List<CandidatoPublicoResponseDTO> executar(Long vagaId, Long empresaId) {
        Vaga vaga = vagaRepository.findById(vagaId)
                .orElseThrow { new NoSuchElementException("Vaga não encontrada para o ID: ${vagaId}") }

        if (vaga.empresa == null || vaga.empresa.id != empresaId) {
            throw new AccessDeniedException("A vaga informada não pertence à empresa autenticada")
        }

        List<Candidato> candidatos = candidatoRepository.findCandidatosInteressadosNaVaga(vagaId)

        return candidatos.stream()
                .map(candidatoMapper::toPublicoResponseDTO)
                .toList()
    }

}
