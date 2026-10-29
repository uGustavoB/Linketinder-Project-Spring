package org.ugustavodev.mscompetencia.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.ugustavodev.mscompetencia.dto.ApiResponseDTO
import org.ugustavodev.mscompetencia.dto.request.VincularCompetenciaRequestDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVinculoResponseDTO
import org.ugustavodev.mscompetencia.security.UsuarioAutenticado
import org.ugustavodev.mscompetencia.usecase.interfaces.IDesvincularCompetenciaCandidatoUseCase
import org.ugustavodev.mscompetencia.usecase.interfaces.IListarCompetenciasCandidatoUseCase
import org.ugustavodev.mscompetencia.usecase.interfaces.IVincularCompetenciaCandidatoUseCase

@RestController
@RequestMapping("/api/competencias/candidato")
class CompetenciaCandidatoController {

    private final IVincularCompetenciaCandidatoUseCase vincularUseCase
    private final IListarCompetenciasCandidatoUseCase listarUseCase
    private final IDesvincularCompetenciaCandidatoUseCase desvincularUseCase

    CompetenciaCandidatoController(
            IVincularCompetenciaCandidatoUseCase vincularUseCase,
            IListarCompetenciasCandidatoUseCase listarUseCase,
            IDesvincularCompetenciaCandidatoUseCase desvincularUseCase) {
        this.vincularUseCase = vincularUseCase
        this.listarUseCase = listarUseCase
        this.desvincularUseCase = desvincularUseCase
    }

    @PostMapping("/{candidatoId}")
    @PreAuthorize("hasRole('CANDIDATO')")
    ResponseEntity<ApiResponseDTO<List<CompetenciaVinculoResponseDTO>>> vincularCompetencia(
            @PathVariable("candidatoId") Long candidatoId,
            @Valid @RequestBody List<VincularCompetenciaRequestDTO> requestDTOs,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    ) {
        if (usuarioAutenticado?.perfilId == null || usuarioAutenticado.perfilId != candidatoId) {
            throw new AccessDeniedException("Você não tem permissão para alterar as competências de outro candidato.")
        }

        List<CompetenciaVinculoResponseDTO> responseDTOs = vincularUseCase.executar(candidatoId, requestDTOs)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.sucesso(responseDTOs, "Competências vinculadas ao candidato com sucesso", HttpStatus.CREATED.value()))
    }

    @GetMapping("/{candidatoId}")
    ResponseEntity<ApiResponseDTO<List<CompetenciaVinculoResponseDTO>>> listarCompetencias(@PathVariable("candidatoId") Long candidatoId) {
        List<CompetenciaVinculoResponseDTO> lista = listarUseCase.executar(candidatoId)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(lista))
    }

    @DeleteMapping("/vinculo/{vinculoId}")
    @PreAuthorize("hasRole('CANDIDATO')")
    ResponseEntity<ApiResponseDTO<Void>> desvincularCompetencia(
            @PathVariable("vinculoId") Long vinculoId,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    ) {
        desvincularUseCase.executar(vinculoId, usuarioAutenticado?.perfilId)
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Vínculo removido com sucesso", HttpStatus.OK.value()))
    }

}
