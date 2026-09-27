package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.response.MatchCandidatoResponseDTO
import org.ugustavodev.msnucleo.mapper.VagaMapper
import org.ugustavodev.msnucleo.model.Curtida
import org.ugustavodev.msnucleo.repositories.CurtidaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IListarMatchesCandidatoUseCase

@Service
class ListarMatchesCandidatoUseCase implements IListarMatchesCandidatoUseCase {
    private final CurtidaRepository curtidaRepository
    private final VagaMapper vagaMapper

    ListarMatchesCandidatoUseCase(CurtidaRepository curtidaRepository, VagaMapper vagaMapper) {
        this.curtidaRepository = curtidaRepository
        this.vagaMapper = vagaMapper
    }

    @Override
    List<MatchCandidatoResponseDTO> executar(Long idCandidato) {
        if (idCandidato == null) {
            throw new IllegalArgumentException("O ID do candidato não pode ser nulo.")
        }

        List<Curtida> curtidas = curtidaRepository.findMatchesByCandidatoId(idCandidato)

        return curtidas.stream().map(curtida -> {
            def dataMatch = curtida.candidatoCurtiuEm.isAfter(curtida.empresaCurtiuEm) ? curtida.candidatoCurtiuEm : curtida.empresaCurtiuEm
            return new MatchCandidatoResponseDTO(
                    vagaMapper.toPublicoResponseDTO(curtida.vaga),
                    curtida.vaga.empresa.nome,
                    dataMatch
            )
        }).toList()
    }
}
