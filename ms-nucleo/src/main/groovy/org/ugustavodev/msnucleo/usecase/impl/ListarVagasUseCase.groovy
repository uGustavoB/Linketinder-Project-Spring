package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.response.VagaResponseDTO
import org.ugustavodev.msnucleo.mapper.VagaMapper
import org.ugustavodev.msnucleo.repositories.VagaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IListarVagasUseCase

@Service
class ListarVagasUseCase implements IListarVagasUseCase {
    private final VagaRepository vagaRepository
    private final VagaMapper vagaMapper

    ListarVagasUseCase(VagaRepository vagaRepository, VagaMapper vagaMapper) {
        this.vagaRepository = vagaRepository
        this.vagaMapper = vagaMapper
    }

    @Override
    List<VagaResponseDTO> executar(Long empresaId) {
        if (empresaId != null) {
            return vagaRepository.findByEmpresaId(empresaId)
                    .collect { vagaMapper.toResponseDTO(it) }
        }
        return vagaRepository.findAll()
                .collect { vagaMapper.toResponseDTO(it) }
    }
}
