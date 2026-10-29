package org.ugustavodev.msnucleo.usecase.impl.matches

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.response.MatchEmpresaResponseDTO
import org.ugustavodev.msnucleo.mapper.CandidatoMapper
import org.ugustavodev.msnucleo.mapper.VagaMapper
import org.ugustavodev.msnucleo.model.Curtida
import org.ugustavodev.msnucleo.repositories.CurtidaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.matches.IListarMatchesEmpresaUseCase

@Service
class ListarMatchesEmpresaUseCase implements IListarMatchesEmpresaUseCase {

    private final CurtidaRepository curtidaRepository
    private final VagaMapper vagaMapper
    private final CandidatoMapper candidatoMapper

    ListarMatchesEmpresaUseCase(
            CurtidaRepository curtidaRepository,
            VagaMapper vagaMapper,
            CandidatoMapper candidatoMapper) {
        this.curtidaRepository = curtidaRepository
        this.vagaMapper = vagaMapper
        this.candidatoMapper = candidatoMapper
    }

    @Override
    List<MatchEmpresaResponseDTO> executar(Long idEmpresa) {
        if (idEmpresa == null) {
            throw new IllegalArgumentException("O ID da empresa não pode ser nulo.")
        }

        List<Curtida> curtidas = curtidaRepository.findMatchesByEmpresaId(idEmpresa)

        return curtidas.stream().map(curtida -> {
            def dataMatch = curtida.candidatoCurtiuEm.isAfter(curtida.empresaCurtiuEm) ? curtida.candidatoCurtiuEm : curtida.empresaCurtiuEm
            return new MatchEmpresaResponseDTO(
                    vagaMapper.toPublicoResponseDTO(curtida.vaga),
                    candidatoMapper.toPublicoResponseDTO(curtida.candidato),
                    curtida.candidato.nome + " " + curtida.candidato.sobrenome,
                    curtida.candidato.usuario?.email,
                    dataMatch
            )
        }).toList()
    }

}
