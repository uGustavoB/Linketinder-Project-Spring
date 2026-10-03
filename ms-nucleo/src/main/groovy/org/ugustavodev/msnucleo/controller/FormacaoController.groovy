package org.ugustavodev.msnucleo.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.dto.request.FormacaoRequestDTO
import org.ugustavodev.msnucleo.dto.ApiResponseDTO
import org.ugustavodev.msnucleo.dto.response.FormacaoResponseDTO
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IAtualizarFormacaoUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.ICadastrarFormacaoUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IDeletarFormacaoUseCase

@RestController
@RequestMapping("/api/candidato/formacoes")
class FormacaoController {
    private final ICadastrarFormacaoUseCase cadastrarFormacaoUseCase
    private final IAtualizarFormacaoUseCase atualizarFormacaoUseCase
    private final IDeletarFormacaoUseCase deletarFormacaoUseCase
    private final CandidatoRepository candidatoRepository

    FormacaoController(
            ICadastrarFormacaoUseCase cadastrarFormacaoUseCase,
            IAtualizarFormacaoUseCase atualizarFormacaoUseCase,
            IDeletarFormacaoUseCase deletarFormacaoUseCase,
            CandidatoRepository candidatoRepository
    ) {
        this.cadastrarFormacaoUseCase = cadastrarFormacaoUseCase
        this.atualizarFormacaoUseCase = atualizarFormacaoUseCase
        this.deletarFormacaoUseCase = deletarFormacaoUseCase
        this.candidatoRepository = candidatoRepository
    }

    @PreAuthorize("hasRole('CANDIDATO')")
    @PostMapping
    ResponseEntity<ApiResponseDTO<FormacaoResponseDTO>> cadastrarFormacao(
            @Valid @RequestBody FormacaoRequestDTO requestDTO,
            @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Candidato candidato = candidatoRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Candidato não encontrado"))
        
        FormacaoResponseDTO responseDTO = cadastrarFormacaoUseCase.executar(requestDTO, candidato.getId())
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.sucesso(responseDTO, "Formação cadastrada com sucesso", HttpStatus.CREATED.value()))
    }

    @PreAuthorize("hasRole('CANDIDATO')")
    @PutMapping("/{id}")
    ResponseEntity<ApiResponseDTO<FormacaoResponseDTO>> atualizarFormacao(
            @PathVariable("id") Long id,
            @Valid @RequestBody FormacaoRequestDTO requestDTO,
            @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Candidato candidato = candidatoRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Candidato não encontrado"))

        FormacaoResponseDTO responseDTO = atualizarFormacaoUseCase.executar(id, requestDTO, candidato.getId())
        return ResponseEntity.ok(ApiResponseDTO.sucesso(responseDTO, "Formação atualizada com sucesso"))
    }

    @PreAuthorize("hasRole('CANDIDATO')")
    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponseDTO<Void>> deletarFormacao(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Candidato candidato = candidatoRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Candidato não encontrado"))

        deletarFormacaoUseCase.executar(id, candidato.getId())
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Formação deletada com sucesso", HttpStatus.OK.value()))
    }
}
