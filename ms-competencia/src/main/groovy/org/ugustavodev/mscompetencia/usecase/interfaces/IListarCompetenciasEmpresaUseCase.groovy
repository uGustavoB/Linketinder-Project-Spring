package org.ugustavodev.mscompetencia.usecase.interfaces
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVinculoResponseDTO
interface IListarCompetenciasEmpresaUseCase { List<CompetenciaVinculoResponseDTO> executar(Long empresaId) }
