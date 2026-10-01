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
@RequestMapping("/api/competencias/candidato")
class CompetenciaCandidatoController {

    private final IVincularCompetenciaCandidatoUseCase vincularUseCase
    private final IListarCompetenciasCandidatoUseCase listarUseCase
    private final IDesvincularCompetenciaCandidatoUseCase desvincularUseCase

    CompetenciaCandidatoController(IVincularCompetenciaCandidatoUseCase vincularUseCase, IListarCompetenciasCandidatoUseCase listarUseCase, IDesvincularCompetenciaCandidatoUseCase desvincularUseCase) {
        this.vincularUseCase = vincularUseCase
        this.listarUseCase = listarUseCase
        this.desvincularUseCase = desvincularUseCase
    }

    @PostMapping("/{candidatoId}")
    ResponseEntity<ApiResponseDTO<List<CompetenciaVinculoResponseDTO>>> vincularCompetencia(
            @PathVariable("candidatoId") Long candidatoId,
            @Valid @RequestBody List<VincularCompetenciaRequestDTO> requestDTOs
    ) {
        List<CompetenciaVinculoResponseDTO> responseDTOs = vincularUseCase.executar(candidatoId, requestDTOs)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.sucesso(responseDTOs, "Competências vinculadas ao candidato com sucesso", HttpStatus.CREATED.value()))
    }

    @GetMapping("/{candidatoId}")
    ResponseEntity<ApiResponseDTO<List<CompetenciaVinculoResponseDTO>>> listarCompetencias(@PathVariable("candidatoId") Long candidatoId) {
        List<CompetenciaVinculoResponseDTO> lista = listarUseCase.executar(candidatoId)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(lista))
    }

    @DeleteMapping("/vinculo/{vinculoId}")
    ResponseEntity<ApiResponseDTO<Void>> desvincularCompetencia(@PathVariable("vinculoId") Long vinculoId) {
        desvincularUseCase.executar(vinculoId)
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Vínculo removido com sucesso", HttpStatus.OK.value()))
    }
}
