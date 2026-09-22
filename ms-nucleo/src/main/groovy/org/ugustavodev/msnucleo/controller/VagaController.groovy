package org.ugustavodev.msnucleo.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.ugustavodev.msnucleo.dto.request.VagaRequestDTO
import org.ugustavodev.msnucleo.dto.response.VagaResponseDTO
import org.ugustavodev.msnucleo.dto.ApiResponseDTO
import org.ugustavodev.msnucleo.usecase.interfaces.IAtualizarVagaUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IBuscarVagaPorIdUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.ICadastrarVagaUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IDeletarVagaUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IListarVagasUseCase

@RestController
@RequestMapping("/api/vaga")
class VagaController {
    private final ICadastrarVagaUseCase cadastrarVagaUseCase
    private final IBuscarVagaPorIdUseCase buscarVagaPorIdUseCase
    private final IListarVagasUseCase listarVagasUseCase
    private final IAtualizarVagaUseCase atualizarVagaUseCase
    private final IDeletarVagaUseCase deletarVagaUseCase

    VagaController(
            ICadastrarVagaUseCase cadastrarVagaUseCase,
            IBuscarVagaPorIdUseCase buscarVagaPorIdUseCase,
            IListarVagasUseCase listarVagasUseCase,
            IAtualizarVagaUseCase atualizarVagaUseCase,
            IDeletarVagaUseCase deletarVagaUseCase
    ) {
        this.cadastrarVagaUseCase = cadastrarVagaUseCase
        this.buscarVagaPorIdUseCase = buscarVagaPorIdUseCase
        this.listarVagasUseCase = listarVagasUseCase
        this.atualizarVagaUseCase = atualizarVagaUseCase
        this.deletarVagaUseCase = deletarVagaUseCase
    }

    @PostMapping
    ResponseEntity<ApiResponseDTO<VagaResponseDTO>> cadastrarVaga(@Valid @RequestBody VagaRequestDTO requestDTO) {
        VagaResponseDTO responseDTO = cadastrarVagaUseCase.executar(requestDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.sucesso(responseDTO, "Vaga cadastrada com sucesso", HttpStatus.CREATED.value()))
    }

    @GetMapping
    ResponseEntity<ApiResponseDTO<List<VagaResponseDTO>>> listarVagas(@RequestParam(name = "empresaId", required = false) Long empresaId) {
        List<VagaResponseDTO> lista = listarVagasUseCase.executar(empresaId)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(lista))
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponseDTO<VagaResponseDTO>> buscarVagaPorId(@PathVariable("id") Long id) {
        VagaResponseDTO responseDTO = buscarVagaPorIdUseCase.executar(id)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(responseDTO))
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponseDTO<VagaResponseDTO>> atualizarVaga(
            @PathVariable("id") Long id,
            @Valid @RequestBody VagaRequestDTO requestDTO
    ) {
        VagaResponseDTO responseDTO = atualizarVagaUseCase.executar(id, requestDTO)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(responseDTO, "Vaga atualizada com sucesso"))
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponseDTO<Void>> deletarVaga(@PathVariable("id") Long id) {
        deletarVagaUseCase.executar(id)
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Vaga deletada com sucesso", HttpStatus.OK.value()))
    }
}
