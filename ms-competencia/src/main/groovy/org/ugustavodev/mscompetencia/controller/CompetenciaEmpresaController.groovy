package org.ugustavodev.mscompetencia.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.ugustavodev.mscompetencia.dto.ApiResponseDTO
import org.ugustavodev.mscompetencia.dto.request.VincularCompetenciaRequestDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVinculoResponseDTO
import org.ugustavodev.mscompetencia.usecase.interfaces.*

@RestController
@RequestMapping("/api/competencias/empresa")
class CompetenciaEmpresaController {

    private final IVincularCompetenciaEmpresaUseCase vincularUseCase
    private final IListarCompetenciasEmpresaUseCase listarUseCase
    private final IDesvincularCompetenciaEmpresaUseCase desvincularUseCase

    CompetenciaEmpresaController(IVincularCompetenciaEmpresaUseCase vincularUseCase, IListarCompetenciasEmpresaUseCase listarUseCase, IDesvincularCompetenciaEmpresaUseCase desvincularUseCase) {
        this.vincularUseCase = vincularUseCase
        this.listarUseCase = listarUseCase
        this.desvincularUseCase = desvincularUseCase
    }

    @PostMapping("/{empresaId}")
    ResponseEntity<ApiResponseDTO<List<CompetenciaVinculoResponseDTO>>> vincularCompetencia(
            @PathVariable("empresaId") Long empresaId,
            @Valid @RequestBody List<VincularCompetenciaRequestDTO> requestDTOs
    ) {
        List<CompetenciaVinculoResponseDTO> responseDTOs = vincularUseCase.executar(empresaId, requestDTOs)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.sucesso(responseDTOs, "Competências vinculadas à empresa com sucesso", HttpStatus.CREATED.value()))
    }

    @GetMapping("/{empresaId}")
    ResponseEntity<ApiResponseDTO<List<CompetenciaVinculoResponseDTO>>> listarCompetencias(@PathVariable("empresaId") Long empresaId) {
        List<CompetenciaVinculoResponseDTO> lista = listarUseCase.executar(empresaId)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(lista))
    }

    @DeleteMapping("/vinculo/{vinculoId}")
    ResponseEntity<ApiResponseDTO<Void>> desvincularCompetencia(@PathVariable("vinculoId") Long vinculoId) {
        desvincularUseCase.executar(vinculoId)
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Vínculo removido com sucesso", HttpStatus.OK.value()))
    }
}
