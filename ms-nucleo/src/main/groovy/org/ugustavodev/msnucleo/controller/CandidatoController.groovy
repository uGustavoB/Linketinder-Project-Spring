package org.ugustavodev.msnucleo.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.ugustavodev.msnucleo.dto.request.CandidatoRequestDTO
import org.ugustavodev.msnucleo.dto.response.CandidatoResponseDTO
import org.ugustavodev.msnucleo.usecase.impl.CadastrarCandidatoUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.ICadastrarCandidatoUseCase

@RestController
@RequestMapping("/api/candidato")
class CandidatoController {
    private final ICadastrarCandidatoUseCase cadastrarCandidatoUseCase

    CandidatoController(CadastrarCandidatoUseCase cadastrarCandidatoUseCase) {
        this.cadastrarCandidatoUseCase = cadastrarCandidatoUseCase
    }

    @PostMapping("/")
    ResponseEntity<CandidatoResponseDTO> cadastrarCandidato(@Valid @RequestBody CandidatoRequestDTO requestDTO) {
        CandidatoResponseDTO responseDTO = cadastrarCandidatoUseCase.executar(requestDTO)
        return ResponseEntity.ok(responseDTO)
    }
}
