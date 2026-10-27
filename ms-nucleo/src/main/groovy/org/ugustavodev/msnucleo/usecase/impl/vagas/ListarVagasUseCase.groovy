package org.ugustavodev.msnucleo.usecase.impl.vagas

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.response.VagaPublicoResponseDTO
import org.ugustavodev.msnucleo.mapper.VagaMapper
import org.ugustavodev.msnucleo.repositories.VagaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.vagas.IListarVagasUseCase

@Service
class ListarVagasUseCase implements IListarVagasUseCase {
    private final VagaRepository vagaRepository
    private final VagaMapper vagaMapper

    ListarVagasUseCase(VagaRepository vagaRepository, VagaMapper vagaMapper) {
        this.vagaRepository = vagaRepository
        this.vagaMapper = vagaMapper
    }

    @Override
    List<VagaPublicoResponseDTO> executar(Long empresaId) {
        if (empresaId != null) {
            return vagaRepository.findByEmpresaId(empresaId)
                    .collect { vagaMapper.toPublicoResponseDTO(it) }
        }
        return vagaRepository.findAll()
                .collect { vagaMapper.toPublicoResponseDTO(it) }
    }
}
