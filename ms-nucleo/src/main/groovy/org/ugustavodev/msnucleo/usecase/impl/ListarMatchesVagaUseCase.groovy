package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.response.MatchVagaResponseDTO
import org.ugustavodev.msnucleo.mapper.CandidatoMapper
import org.ugustavodev.msnucleo.model.Curtida
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.CurtidaRepository
import org.ugustavodev.msnucleo.repositories.VagaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IListarMatchesVagaUseCase

@Service
class ListarMatchesVagaUseCase implements IListarMatchesVagaUseCase {
    private final CurtidaRepository curtidaRepository
    private final VagaRepository vagaRepository
    private final CandidatoMapper candidatoMapper

    ListarMatchesVagaUseCase(CurtidaRepository curtidaRepository, VagaRepository vagaRepository, CandidatoMapper candidatoMapper) {
        this.curtidaRepository = curtidaRepository
        this.vagaRepository = vagaRepository
        this.candidatoMapper = candidatoMapper
    }

    @Override
    List<MatchVagaResponseDTO> executar(Long idVaga, Long idEmpresaLogada) {
        if (idVaga == null || idEmpresaLogada == null) {
            throw new IllegalArgumentException("IDs não podem ser nulos.")
        }

        Vaga vaga = vagaRepository.findById(idVaga)
                .orElseThrow(() -> new NoSuchElementException("Vaga não encontrada."))

        if (vaga.getEmpresa().getId() != idEmpresaLogada) {
            throw new AccessDeniedException("Esta vaga não pertence à sua empresa.")
        }

        List<Curtida> curtidas = curtidaRepository.findMatchesByVagaIdAndEmpresaId(idVaga, idEmpresaLogada)

        return curtidas.stream().map(curtida -> {
            def dataMatch = curtida.candidatoCurtiuEm.isAfter(curtida.empresaCurtiuEm) ? curtida.candidatoCurtiuEm : curtida.empresaCurtiuEm
            return new MatchVagaResponseDTO(
                    candidatoMapper.toPublicoResponseDTO(curtida.candidato),
                    curtida.candidato.nome + " " + curtida.candidato.sobrenome,
                    curtida.candidato.usuario.email,
                    dataMatch
            )
        }).toList()
    }
}
