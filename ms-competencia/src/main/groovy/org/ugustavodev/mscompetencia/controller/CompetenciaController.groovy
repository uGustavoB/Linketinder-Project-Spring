package org.ugustavodev.mscompetencia.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.ugustavodev.mscompetencia.dto.ApiResponseDTO
import org.ugustavodev.mscompetencia.dto.request.CompetenciaRequestDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaResponseDTO
import org.ugustavodev.mscompetencia.usecase.interfaces.ICadastrarCompetenciaUseCase
import org.ugustavodev.mscompetencia.usecase.interfaces.IListarCompetenciasUseCase

@RestController
@RequestMapping("/api/competencias")
class CompetenciaController {

    private final ICadastrarCompetenciaUseCase cadastrarCompetenciaUseCase
    private final IListarCompetenciasUseCase listarCompetenciasUseCase

    CompetenciaController(
            ICadastrarCompetenciaUseCase cadastrarCompetenciaUseCase,
            IListarCompetenciasUseCase listarCompetenciasUseCase) {
        this.cadastrarCompetenciaUseCase = cadastrarCompetenciaUseCase
        this.listarCompetenciasUseCase = listarCompetenciasUseCase
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CANDIDATO', 'EMPRESA')")
    ResponseEntity<ApiResponseDTO<CompetenciaResponseDTO>> cadastrarCompetencia(@Valid @RequestBody CompetenciaRequestDTO requestDTO) {
        CompetenciaResponseDTO responseDTO = cadastrarCompetenciaUseCase.executar(requestDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.sucesso(responseDTO, "Competência global criada com sucesso", HttpStatus.CREATED.value()))
    }

    @GetMapping
    ResponseEntity<ApiResponseDTO<List<CompetenciaResponseDTO>>> listarCompetencias() {
        List<CompetenciaResponseDTO> lista = listarCompetenciasUseCase.executar()
        return ResponseEntity.ok(ApiResponseDTO.sucesso(lista))
    }

}
