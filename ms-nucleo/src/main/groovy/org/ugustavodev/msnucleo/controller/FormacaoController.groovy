package org.ugustavodev.msnucleo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
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
@Tag(name = "4. Formações Acadêmicas", description = "CRUD de formações acadêmicas vinculadas ao perfil do candidato autenticado")
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

    @Operation(
            summary = "Cadastra uma nova formação acadêmica",
            description = "Adiciona um registro de formação acadêmica ao perfil do candidato autenticado (`ROLE_CANDIDATO`)."
    )
    @ApiResponses([
            @ApiResponse(responseCode = "201", description = "Formação cadastrada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso restrito a usuários com papel CANDIDATO"),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos informados")
    ])
    @PreAuthorize("hasRole('CANDIDATO')")
    @PostMapping
    ResponseEntity<ApiResponseDTO<FormacaoResponseDTO>> cadastrarFormacao(
            @Valid @RequestBody FormacaoRequestDTO requestDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Candidato candidato = candidatoRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Candidato não encontrado"))
        
        FormacaoResponseDTO responseDTO = cadastrarFormacaoUseCase.executar(requestDTO, candidato.getId())
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.sucesso(responseDTO, "Formação cadastrada com sucesso", HttpStatus.CREATED.value()))
    }

    @Operation(
            summary = "Atualiza uma formação acadêmica existente",
            description = "Atualiza os dados de uma formação acadêmica, validando se ela pertence ao candidato autenticado."
    )
    @ApiResponses([
            @ApiResponse(responseCode = "200", description = "Formação atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Violação de propriedade da formação ou dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso restrito a usuários com papel CANDIDATO"),
            @ApiResponse(responseCode = "404", description = "Formação não encontrada"),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos informados")
    ])
    @PreAuthorize("hasRole('CANDIDATO')")
    @PutMapping("/{id}")
    ResponseEntity<ApiResponseDTO<FormacaoResponseDTO>> atualizarFormacao(
            @Parameter(description = "ID da formação acadêmica", example = "1") @PathVariable("id") Long id,
            @Valid @RequestBody FormacaoRequestDTO requestDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Candidato candidato = candidatoRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Candidato não encontrado"))

        FormacaoResponseDTO responseDTO = atualizarFormacaoUseCase.executar(id, requestDTO, candidato.getId())
        return ResponseEntity.ok(ApiResponseDTO.sucesso(responseDTO, "Formação atualizada com sucesso"))
    }

    @Operation(
            summary = "Remove uma formação acadêmica",
            description = "Deleta uma formação acadêmica pertencente ao candidato autenticado."
    )
    @ApiResponses([
            @ApiResponse(responseCode = "200", description = "Formação deletada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Violação de propriedade da formação"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso restrito a usuários com papel CANDIDATO"),
            @ApiResponse(responseCode = "404", description = "Formação não encontrada")
    ])
    @PreAuthorize("hasRole('CANDIDATO')")
    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponseDTO<Void>> deletarFormacao(
            @Parameter(description = "ID da formação acadêmica", example = "1") @PathVariable("id") Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Candidato candidato = candidatoRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Candidato não encontrado"))

        deletarFormacaoUseCase.executar(id, candidato.getId())
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Formação deletada com sucesso", HttpStatus.OK.value()))
    }
}
