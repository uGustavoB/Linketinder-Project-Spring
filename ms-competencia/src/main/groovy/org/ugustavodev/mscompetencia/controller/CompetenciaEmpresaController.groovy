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
import org.ugustavodev.mscompetencia.usecase.interfaces.IDesvincularCompetenciaEmpresaUseCase
import org.ugustavodev.mscompetencia.usecase.interfaces.IListarCompetenciasEmpresaUseCase
import org.ugustavodev.mscompetencia.usecase.interfaces.IVincularCompetenciaEmpresaUseCase

@RestController
@RequestMapping("/api/competencias/empresa")
class CompetenciaEmpresaController {

    private final IVincularCompetenciaEmpresaUseCase vincularUseCase
    private final IListarCompetenciasEmpresaUseCase listarUseCase
    private final IDesvincularCompetenciaEmpresaUseCase desvincularUseCase

    CompetenciaEmpresaController(
            IVincularCompetenciaEmpresaUseCase vincularUseCase,
            IListarCompetenciasEmpresaUseCase listarUseCase,
            IDesvincularCompetenciaEmpresaUseCase desvincularUseCase) {
        this.vincularUseCase = vincularUseCase
        this.listarUseCase = listarUseCase
        this.desvincularUseCase = desvincularUseCase
    }

    @PostMapping("/{empresaId}")
    @PreAuthorize("hasRole('EMPRESA')")
    ResponseEntity<ApiResponseDTO<List<CompetenciaVinculoResponseDTO>>> vincularCompetencia(
            @PathVariable("empresaId") Long empresaId,
            @Valid @RequestBody List<VincularCompetenciaRequestDTO> requestDTOs,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    ) {
        if (usuarioAutenticado?.perfilId == null || usuarioAutenticado.perfilId != empresaId) {
            throw new AccessDeniedException("Você não tem permissão para alterar as competências de outra empresa.")
        }

        List<CompetenciaVinculoResponseDTO> responseDTOs = vincularUseCase.executar(empresaId, requestDTOs)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.sucesso(responseDTOs, "Competências vinculadas à empresa com sucesso", HttpStatus.CREATED.value()))
    }

    @GetMapping("/{empresaId}")
    ResponseEntity<ApiResponseDTO<List<CompetenciaVinculoResponseDTO>>> listarCompetencias(@PathVariable("empresaId") Long empresaId) {
        List<CompetenciaVinculoResponseDTO> lista = listarUseCase.executar(empresaId)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(lista))
    }

    @DeleteMapping("/vinculo/{vinculoId}")
    @PreAuthorize("hasRole('EMPRESA')")
    ResponseEntity<ApiResponseDTO<Void>> desvincularCompetencia(
            @PathVariable("vinculoId") Long vinculoId,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    ) {
        desvincularUseCase.executar(vinculoId, usuarioAutenticado?.perfilId)
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Vínculo removido com sucesso", HttpStatus.OK.value()))
    }

}
