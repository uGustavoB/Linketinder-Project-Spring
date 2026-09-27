package org.ugustavodev.msnucleo.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.dto.request.CandidatoRequestDTO
import org.ugustavodev.msnucleo.dto.response.CandidatoResponseDTO
import org.ugustavodev.msnucleo.dto.response.CandidatoPublicoResponseDTO
import org.ugustavodev.msnucleo.dto.response.MatchCandidatoResponseDTO
import org.ugustavodev.msnucleo.dto.ApiResponseDTO
import org.ugustavodev.msnucleo.usecase.interfaces.IAtualizarCandidatoUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IBuscarCandidatoPorIdUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.ICadastrarCandidatoUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IDeletarCandidatoUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IListarCandidatosUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IListarMatchesCandidatoUseCase

@RestController
@RequestMapping("/api/candidato")
class CandidatoController {
    private final ICadastrarCandidatoUseCase cadastrarCandidatoUseCase
    private final IBuscarCandidatoPorIdUseCase buscarCandidatoPorIdUseCase
    private final IListarCandidatosUseCase listarCandidatosUseCase
    private final IAtualizarCandidatoUseCase atualizarCandidatoUseCase
    private final IDeletarCandidatoUseCase deletarCandidatoUseCase
    private final IListarMatchesCandidatoUseCase listarMatchesCandidatoUseCase

    CandidatoController(
            ICadastrarCandidatoUseCase cadastrarCandidatoUseCase,
            IBuscarCandidatoPorIdUseCase buscarCandidatoPorIdUseCase,
            IListarCandidatosUseCase listarCandidatosUseCase,
            IAtualizarCandidatoUseCase atualizarCandidatoUseCase,
            IDeletarCandidatoUseCase deletarCandidatoUseCase,
            IListarMatchesCandidatoUseCase listarMatchesCandidatoUseCase
    ) {
        this.cadastrarCandidatoUseCase = cadastrarCandidatoUseCase
        this.buscarCandidatoPorIdUseCase = buscarCandidatoPorIdUseCase
        this.listarCandidatosUseCase = listarCandidatosUseCase
        this.atualizarCandidatoUseCase = atualizarCandidatoUseCase
        this.deletarCandidatoUseCase = deletarCandidatoUseCase
        this.listarMatchesCandidatoUseCase = listarMatchesCandidatoUseCase
    }

    @PostMapping
    ResponseEntity<ApiResponseDTO<CandidatoResponseDTO>> cadastrarCandidato(@Valid @RequestBody CandidatoRequestDTO requestDTO) {
        CandidatoResponseDTO responseDTO = cadastrarCandidatoUseCase.executar(requestDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.sucesso(responseDTO, "Candidato cadastrado com sucesso", HttpStatus.CREATED.value()))
    }

    @GetMapping
    ResponseEntity<ApiResponseDTO<List<CandidatoPublicoResponseDTO>>> listarCandidatos() {
        List<CandidatoPublicoResponseDTO> lista = listarCandidatosUseCase.executar()
        return ResponseEntity.ok(ApiResponseDTO.sucesso(lista))
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponseDTO<CandidatoResponseDTO>> buscarCandidatoPorId(@PathVariable("id") Long id) {
        CandidatoResponseDTO responseDTO = buscarCandidatoPorIdUseCase.executar(id)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(responseDTO))
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponseDTO<CandidatoResponseDTO>> atualizarCandidato(
            @PathVariable("id") Long id,
            @Valid @RequestBody CandidatoRequestDTO requestDTO
    ) {
        CandidatoResponseDTO responseDTO = atualizarCandidatoUseCase.executar(id, requestDTO)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(responseDTO, "Candidato atualizado com sucesso"))
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponseDTO<Void>> deletarCandidato(@PathVariable("id") Long id) {
        deletarCandidatoUseCase.executar(id)
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Candidato deletado com sucesso", HttpStatus.OK.value()))
    }

    @PreAuthorize("hasRole('CANDIDATO')")
    @GetMapping("/matches")
    ResponseEntity<ApiResponseDTO<List<MatchCandidatoResponseDTO>>> listarMatches(@AuthenticationPrincipal Usuario usuarioAutenticado) {
        Long idCandidato = usuarioAutenticado.getCandidato().getId()
        List<MatchCandidatoResponseDTO> matches = listarMatchesCandidatoUseCase.executar(idCandidato)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(matches))
    }
}
