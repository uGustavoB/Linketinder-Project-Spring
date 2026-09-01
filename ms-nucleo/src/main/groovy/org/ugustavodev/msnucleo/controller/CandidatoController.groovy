package org.ugustavodev.msnucleo.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.ugustavodev.msnucleo.dto.request.CandidatoRequestDTO
import org.ugustavodev.msnucleo.dto.response.CandidatoResponseDTO
import org.ugustavodev.msnucleo.usecase.interfaces.IAtualizarCandidatoUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IBuscarCandidatoPorIdUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.ICadastrarCandidatoUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IDeletarCandidatoUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IListarCandidatosUseCase

@RestController
@RequestMapping("/api/candidato")
class CandidatoController {
    private final ICadastrarCandidatoUseCase cadastrarCandidatoUseCase
    private final IBuscarCandidatoPorIdUseCase buscarCandidatoPorIdUseCase
    private final IListarCandidatosUseCase listarCandidatosUseCase
    private final IAtualizarCandidatoUseCase atualizarCandidatoUseCase
    private final IDeletarCandidatoUseCase deletarCandidatoUseCase

    CandidatoController(
            ICadastrarCandidatoUseCase cadastrarCandidatoUseCase,
            IBuscarCandidatoPorIdUseCase buscarCandidatoPorIdUseCase,
            IListarCandidatosUseCase listarCandidatosUseCase,
            IAtualizarCandidatoUseCase atualizarCandidatoUseCase,
            IDeletarCandidatoUseCase deletarCandidatoUseCase
    ) {
        this.cadastrarCandidatoUseCase = cadastrarCandidatoUseCase
        this.buscarCandidatoPorIdUseCase = buscarCandidatoPorIdUseCase
        this.listarCandidatosUseCase = listarCandidatosUseCase
        this.atualizarCandidatoUseCase = atualizarCandidatoUseCase
        this.deletarCandidatoUseCase = deletarCandidatoUseCase
    }

    @PostMapping
    ResponseEntity<CandidatoResponseDTO> cadastrarCandidato(@Valid @RequestBody CandidatoRequestDTO requestDTO) {
        CandidatoResponseDTO responseDTO = cadastrarCandidatoUseCase.executar(requestDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO)
    }

    @GetMapping
    ResponseEntity<List<CandidatoResponseDTO>> listarCandidatos() {
        List<CandidatoResponseDTO> lista = listarCandidatosUseCase.executar()
        return ResponseEntity.ok(lista)
    }

    @GetMapping("/{id}")
    ResponseEntity<CandidatoResponseDTO> buscarCandidatoPorId(@PathVariable("id") Long id) {
        CandidatoResponseDTO responseDTO = buscarCandidatoPorIdUseCase.executar(id)
        return ResponseEntity.ok(responseDTO)
    }

    @PutMapping("/{id}")
    ResponseEntity<CandidatoResponseDTO> atualizarCandidato(
            @PathVariable("id") Long id,
            @Valid @RequestBody CandidatoRequestDTO requestDTO
    ) {
        CandidatoResponseDTO responseDTO = atualizarCandidatoUseCase.executar(id, requestDTO)
        return ResponseEntity.ok(responseDTO)
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deletarCandidato(@PathVariable("id") Long id) {
        deletarCandidatoUseCase.executar(id)
        return ResponseEntity.noContent().build()
    }
}
