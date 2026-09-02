package org.ugustavodev.msnucleo.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.ugustavodev.msnucleo.dto.request.EmpresaRequestDTO
import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO
import org.ugustavodev.msnucleo.usecase.interfaces.IAtualizarEmpresaUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IBuscarEmpresaPorIdUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.ICadastrarEmpresaUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IDeletarEmpresaUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IListarEmpresasUseCase

@RestController
@RequestMapping("/api/empresa")
class EmpresaController {
    private final ICadastrarEmpresaUseCase cadastrarEmpresaUseCase
    private final IBuscarEmpresaPorIdUseCase buscarEmpresaPorIdUseCase
    private final IListarEmpresasUseCase listarEmpresasUseCase
    private final IAtualizarEmpresaUseCase atualizarEmpresaUseCase
    private final IDeletarEmpresaUseCase deletarEmpresaUseCase

    EmpresaController(
            ICadastrarEmpresaUseCase cadastrarEmpresaUseCase,
            IBuscarEmpresaPorIdUseCase buscarEmpresaPorIdUseCase,
            IListarEmpresasUseCase listarEmpresasUseCase,
            IAtualizarEmpresaUseCase atualizarEmpresaUseCase,
            IDeletarEmpresaUseCase deletarEmpresaUseCase
    ) {
        this.cadastrarEmpresaUseCase = cadastrarEmpresaUseCase
        this.buscarEmpresaPorIdUseCase = buscarEmpresaPorIdUseCase
        this.listarEmpresasUseCase = listarEmpresasUseCase
        this.atualizarEmpresaUseCase = atualizarEmpresaUseCase
        this.deletarEmpresaUseCase = deletarEmpresaUseCase
    }

    @PostMapping
    ResponseEntity<EmpresaResponseDTO> cadastrarEmpresa(@Valid @RequestBody EmpresaRequestDTO requestDTO) {
        EmpresaResponseDTO responseDTO = cadastrarEmpresaUseCase.executar(requestDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO)
    }

    @GetMapping
    ResponseEntity<List<EmpresaResponseDTO>> listarEmpresas() {
        List<EmpresaResponseDTO> lista = listarEmpresasUseCase.executar()
        return ResponseEntity.ok(lista)
    }

    @GetMapping("/{id}")
    ResponseEntity<EmpresaResponseDTO> buscarEmpresaPorId(@PathVariable("id") Long id) {
        EmpresaResponseDTO responseDTO = buscarEmpresaPorIdUseCase.executar(id)
        return ResponseEntity.ok(responseDTO)
    }

    @PutMapping("/{id}")
    ResponseEntity<EmpresaResponseDTO> atualizarEmpresa(
            @PathVariable("id") Long id,
            @Valid @RequestBody EmpresaRequestDTO requestDTO
    ) {
        EmpresaResponseDTO responseDTO = atualizarEmpresaUseCase.executar(id, requestDTO)
        return ResponseEntity.ok(responseDTO)
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deletarEmpresa(@PathVariable("id") Long id) {
        deletarEmpresaUseCase.executar(id)
        return ResponseEntity.noContent().build()
    }
}
