package org.ugustavodev.msnucleo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
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
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IAtualizarCandidatoUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IBuscarCandidatoPorIdUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.ICadastrarCandidatoUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IDeletarCandidatoUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IListarCandidatosUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IListarMatchesCandidatoUseCase

@RestController
@RequestMapping("/api/candidato")
@Tag(name = "3. Candidatos", description = "Gestão cadastral de candidatos, listagem pública anonimizada e consulta de matches do candidato")
class CandidatoController {
    private final ICadastrarCandidatoUseCase cadastrarCandidatoUseCase
    private final IBuscarCandidatoPorIdUseCase buscarCandidatoPorIdUseCase
    private final IListarCandidatosUseCase listarCandidatosUseCase
    private final IAtualizarCandidatoUseCase atualizarCandidatoUseCase
    private final IDeletarCandidatoUseCase deletarCandidatoUseCase
    private final IListarMatchesCandidatoUseCase listarMatchesCandidatoUseCase
    private final CandidatoRepository candidatoRepository

    CandidatoController(
            ICadastrarCandidatoUseCase cadastrarCandidatoUseCase,
            IBuscarCandidatoPorIdUseCase buscarCandidatoPorIdUseCase,
            IListarCandidatosUseCase listarCandidatosUseCase,
            IAtualizarCandidatoUseCase atualizarCandidatoUseCase,
            IDeletarCandidatoUseCase deletarCandidatoUseCase,
            IListarMatchesCandidatoUseCase listarMatchesCandidatoUseCase,
            CandidatoRepository candidatoRepository
    ) {
        this.cadastrarCandidatoUseCase = cadastrarCandidatoUseCase
        this.buscarCandidatoPorIdUseCase = buscarCandidatoPorIdUseCase
        this.listarCandidatosUseCase = listarCandidatosUseCase
        this.atualizarCandidatoUseCase = atualizarCandidatoUseCase
        this.deletarCandidatoUseCase = deletarCandidatoUseCase
        this.listarMatchesCandidatoUseCase = listarMatchesCandidatoUseCase
        this.candidatoRepository = candidatoRepository
    }

    @Operation(
            summary = "Cadastra um novo candidato (Público)",
            description = "Cria uma nova conta de candidato com perfil `ROLE_CANDIDATO`. O e-mail e o CPF devem ser únicos no sistema."
    )
    @SecurityRequirements
    @ApiResponses([
            @ApiResponse(responseCode = "201", description = "Candidato cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "E-mail/CPF já cadastrado ou senha inválida"),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos informados")
    ])
    @PostMapping
    ResponseEntity<ApiResponseDTO<CandidatoResponseDTO>> cadastrarCandidato(@Valid @RequestBody CandidatoRequestDTO requestDTO) {
        CandidatoResponseDTO responseDTO = cadastrarCandidatoUseCase.executar(requestDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.sucesso(responseDTO, "Candidato cadastrado com sucesso", HttpStatus.CREATED.value()))
    }

    @Operation(
            summary = "Lista candidatos de forma anonimizada (Pré-Match)",
            description = "Retorna `CandidatoPublicoResponseDTO` contendo apenas ID, país, estado, descrição e formações acadêmicas. Oculta nome, sobrenome, e-mail, CPF, data de nascimento e CEP em respeito ao Princípio do Anonimato."
    )
    @ApiResponses([
            @ApiResponse(responseCode = "200", description = "Lista de candidatos anônimos retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    ])
    @GetMapping
    ResponseEntity<ApiResponseDTO<List<CandidatoPublicoResponseDTO>>> listarCandidatos() {
        List<CandidatoPublicoResponseDTO> lista = listarCandidatosUseCase.executar()
        return ResponseEntity.ok(ApiResponseDTO.sucesso(lista))
    }

    @Operation(
            summary = "Busca dados completos do candidato por ID (Restrito ao Titular)",
            description = "Retorna `CandidatoResponseDTO` com todos os dados pessoais. Bloqueia o acesso de empresas (`ROLE_EMPRESA`) e de outros candidatos com HTTP 403."
    )
    @ApiResponses([
            @ApiResponse(responseCode = "200", description = "Dados do candidato retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado: empresas ou outros candidatos não podem visualizar dados sensíveis"),
            @ApiResponse(responseCode = "404", description = "Candidato não encontrado")
    ])
    @GetMapping("/{id}")
    ResponseEntity<ApiResponseDTO<CandidatoResponseDTO>> buscarCandidatoPorId(
            @Parameter(description = "ID do candidato", example = "1") @PathVariable("id") Long id
    ) {
        CandidatoResponseDTO responseDTO = buscarCandidatoPorIdUseCase.executar(id)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(responseDTO))
    }

    @Operation(
            summary = "Atualiza os dados cadastrais do próprio candidato",
            description = "Permite ao candidato titular atualizar seus dados pessoais e, opcionalmente, sua senha."
    )
    @ApiResponses([
            @ApiResponse(responseCode = "200", description = "Candidato atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Conflito de e-mail/CPF ou dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado para alterar perfil de terceiro"),
            @ApiResponse(responseCode = "404", description = "Candidato não encontrado"),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos informados")
    ])
    @PutMapping("/{id}")
    ResponseEntity<ApiResponseDTO<CandidatoResponseDTO>> atualizarCandidato(
            @Parameter(description = "ID do candidato", example = "1") @PathVariable("id") Long id,
            @Valid @RequestBody CandidatoRequestDTO requestDTO
    ) {
        CandidatoResponseDTO responseDTO = atualizarCandidatoUseCase.executar(id, requestDTO)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(responseDTO, "Candidato atualizado com sucesso"))
    }

    @Operation(
            summary = "Deleta a conta do próprio candidato",
            description = "Remove em cascata todas as curtidas, formações acadêmicas, perfil e credenciais de usuário do candidato titular."
    )
    @ApiResponses([
            @ApiResponse(responseCode = "200", description = "Candidato deletado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado para deletar perfil de terceiro"),
            @ApiResponse(responseCode = "404", description = "Candidato não encontrado")
    ])
    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponseDTO<Void>> deletarCandidato(
            @Parameter(description = "ID do candidato", example = "1") @PathVariable("id") Long id
    ) {
        deletarCandidatoUseCase.executar(id)
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Candidato deletado com sucesso", HttpStatus.OK.value()))
    }

    @Operation(
            summary = "Lista os matches consolidados do candidato autenticado (Pós-Match)",
            description = "Retorna todas as vagas em que houve interesse mútuo (`candidatoCurtiuEm != null` e `empresaCurtiuEm != null`), revelando o nome e o e-mail de contato da empresa contratante."
    )
    @ApiResponses([
            @ApiResponse(responseCode = "200", description = "Lista de matches retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso restrito a usuários com papel CANDIDATO")
    ])
    @PreAuthorize("hasRole('CANDIDATO')")
    @GetMapping("/matches")
    ResponseEntity<ApiResponseDTO<List<MatchCandidatoResponseDTO>>> listarMatches(
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Candidato candidato = candidatoRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Candidato não encontrado"))
        Long idCandidato = candidato.getId()
        
        List<MatchCandidatoResponseDTO> matches = listarMatchesCandidatoUseCase.executar(idCandidato)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(matches))
    }
}
