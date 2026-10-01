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
@RequestMapping("/api/competencias/vaga")
class CompetenciaVagaController {

    private final IVincularCompetenciaVagaUseCase vincularUseCase
    private final IListarCompetenciasVagaUseCase listarUseCase
    private final IDesvincularCompetenciaVagaUseCase desvincularUseCase

    CompetenciaVagaController(IVincularCompetenciaVagaUseCase vincularUseCase, IListarCompetenciasVagaUseCase listarUseCase, IDesvincularCompetenciaVagaUseCase desvincularUseCase) {
        this.vincularUseCase = vincularUseCase
        this.listarUseCase = listarUseCase
        this.desvincularUseCase = desvincularUseCase
    }

    @PostMapping("/{vagaId}")
    ResponseEntity<ApiResponseDTO<List<CompetenciaVinculoResponseDTO>>> vincularCompetencia(
            @PathVariable("vagaId") Long vagaId,
            @Valid @RequestBody List<VincularCompetenciaRequestDTO> requestDTOs
    ) {
        List<CompetenciaVinculoResponseDTO> responseDTOs = vincularUseCase.executar(vagaId, requestDTOs)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.sucesso(responseDTOs, "Competências vinculadas à vaga com sucesso", HttpStatus.CREATED.value()))
    }

    @GetMapping("/{vagaId}")
    ResponseEntity<ApiResponseDTO<List<CompetenciaVinculoResponseDTO>>> listarCompetencias(@PathVariable("vagaId") Long vagaId) {
        List<CompetenciaVinculoResponseDTO> lista = listarUseCase.executar(vagaId)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(lista))
    }

    @DeleteMapping("/vinculo/{vinculoId}")
    ResponseEntity<ApiResponseDTO<Void>> desvincularCompetencia(@PathVariable("vinculoId") Long vinculoId) {
        desvincularUseCase.executar(vinculoId)
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Vínculo removido com sucesso", HttpStatus.OK.value()))
    }
}
