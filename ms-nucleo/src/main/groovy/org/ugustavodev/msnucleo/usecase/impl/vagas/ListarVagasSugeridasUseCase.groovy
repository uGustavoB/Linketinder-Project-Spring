package org.ugustavodev.msnucleo.usecase.impl.vagas

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.response.VagaPublicoResponseDTO
import org.ugustavodev.msnucleo.mapper.VagaMapper
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.VagaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IListarVagasSugeridasUseCase

@Service
class ListarVagasSugeridasUseCase implements IListarVagasSugeridasUseCase {
    private final VagaRepository vagaRepository
    private final VagaMapper vagaMapper

    ListarVagasSugeridasUseCase(VagaRepository vagaRepository, VagaMapper vagaMapper) {
        this.vagaRepository = vagaRepository
        this.vagaMapper = vagaMapper
    }

    @Override
    List<VagaPublicoResponseDTO> executar(Long candidatoId) {
        if (candidatoId == null) {
            throw new IllegalArgumentException("O ID do candidato não pode ser nulo")
        }

        List<Vaga> vagas = vagaRepository.findVagasSugeridasParaCandidato(candidatoId)
        
        return vagas.stream()
                .map(vagaMapper::toPublicoResponseDTO)
                .toList()
    }
}
