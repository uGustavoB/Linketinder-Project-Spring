package org.ugustavodev.msnucleo.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.ugustavodev.msnucleo.dto.request.EmpresaRequestDTO
import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO
import org.ugustavodev.msnucleo.usecase.interfaces.ICadastrarEmpresaUseCase

@RestController
@RequestMapping("/api/empresa")
class EmpresaController {
    private final ICadastrarEmpresaUseCase cadastrarEmpresaUseCase

    EmpresaController(ICadastrarEmpresaUseCase cadastrarEmpresaUseCase) {
        this.cadastrarEmpresaUseCase = cadastrarEmpresaUseCase
    }

    @PostMapping
    ResponseEntity<EmpresaResponseDTO> cadastrarEmpresa(@Valid @RequestBody EmpresaRequestDTO requestDTO) {
        EmpresaResponseDTO responseDTO = cadastrarEmpresaUseCase.executar(requestDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO)
    }
}
