package org.ugustavodev.msnucleo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.dto.request.FormacaoRequestDTO
import org.ugustavodev.msnucleo.dto.ApiResponseDTO
import org.ugustavodev.msnucleo.dto.response.FormacaoResponseDTO
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.usecase.interfaces.formacao.IAtualizarFormacaoUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.formacao.ICadastrarFormacaoUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.formacao.IDeletarFormacaoUseCase

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
            CandidatoRepository candidatoRepository) {
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
            @ApiResponse(
                    responseCode = "201",
                    description = "Formação cadastrada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Formação Cadastrada",
                                    value = """{
                                        "codigo": 201,
                                        "mensagem": "Formação cadastrada com sucesso",
                                        "tipo": "Sucesso",
                                        "dados": {
                                            "id": 1,
                                            "instituicao": "Universidade de São Paulo (USP)",
                                            "curso": "Ciência da Computação",
                                            "nivel": "Bacharelado",
                                            "status": "Concluído",
                                            "dataInicio": "2020-02-01",
                                            "dataConclusao": "2023-12-15"
                                        }
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Não Autenticado",
                                    value = """{
                                        "codigo": 401,
                                        "mensagem": "Você não está autenticado. Informe um token JWT válido.",
                                        "tipo": "Não Autorizado"
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso restrito a usuários com papel CANDIDATO",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Acesso Negado",
                                    value = """{
                                        "codigo": 403,
                                        "mensagem": "Você não tem permissão para acessar este recurso.",
                                        "tipo": "Acesso Negado"
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Erro de validação nos campos informados",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Erro de Validação",
                                    value = """{
                                        "codigo": 422,
                                        "mensagem": "Verifique os campos e tente novamente.",
                                        "tipo": "Erro de Validação",
                                        "erros": [
                                            {
                                                "campo": "instituicao",
                                                "mensagem": "Instituição é obrigatória"
                                            },
                                            {
                                                "campo": "curso",
                                                "mensagem": "Curso é obrigatório"
                                            }
                                        ]
                                    }"""
                            )
                    )
            )
    ])
    @PreAuthorize("hasRole('CANDIDATO')")
    @PostMapping
    ResponseEntity<ApiResponseDTO<FormacaoResponseDTO>> cadastrarFormacao(
            @Valid @RequestBody FormacaoRequestDTO requestDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Candidato candidato = candidatoRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow { new RuntimeException("Candidato não encontrado") }
        
        FormacaoResponseDTO responseDTO = cadastrarFormacaoUseCase.executar(requestDTO, candidato.getId())
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.sucesso(responseDTO, "Formação cadastrada com sucesso", HttpStatus.CREATED.value()))
    }

    @Operation(
            summary = "Atualiza uma formação acadêmica existente",
            description = "Atualiza os dados de uma formação acadêmica, validando se ela pertence ao candidato autenticado."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Formação atualizada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Formação Atualizada",
                                    value = """{
                                        "codigo": 200,
                                        "mensagem": "Formação atualizada com sucesso",
                                        "tipo": "Sucesso",
                                        "dados": {
                                            "id": 1,
                                            "instituicao": "Universidade de São Paulo (USP)",
                                            "curso": "Ciência da Computação",
                                            "nivel": "Bacharelado",
                                            "status": "Concluído",
                                            "dataInicio": "2020-02-01",
                                            "dataConclusao": "2023-12-15"
                                        }
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Violação de propriedade da formação ou dados inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Formação de Outro Candidato",
                                    value = """{
                                        "codigo": 400,
                                        "mensagem": "Você não tem permissão para editar esta formação.",
                                        "tipo": "Requisição Inválida"
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Não Autenticado",
                                    value = """{
                                        "codigo": 401,
                                        "mensagem": "Você não está autenticado. Informe um token JWT válido.",
                                        "tipo": "Não Autorizado"
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso restrito a usuários com papel CANDIDATO",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Acesso Negado",
                                    value = """{
                                        "codigo": 403,
                                        "mensagem": "Você não tem permissão para acessar este recurso.",
                                        "tipo": "Acesso Negado"
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Formação não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Formação Não Encontrada",
                                    value = """{
                                        "codigo": 404,
                                        "mensagem": "Formação não encontrada para o ID: 1",
                                        "tipo": "Recurso Não Encontrado"
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Erro de validação nos campos informados",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Erro de Validação",
                                    value = """{
                                        "codigo": 422,
                                        "mensagem": "Verifique os campos e tente novamente.",
                                        "tipo": "Erro de Validação",
                                        "erros": [
                                            {
                                                "campo": "status",
                                                "mensagem": "Status é obrigatório"
                                            }
                                        ]
                                    }"""
                            )
                    )
            )
    ])
    @PreAuthorize("hasRole('CANDIDATO')")
    @PutMapping("/{id}")
    ResponseEntity<ApiResponseDTO<FormacaoResponseDTO>> atualizarFormacao(
            @Parameter(description = "ID da formação acadêmica", example = "1") @PathVariable("id") Long id,
            @Valid @RequestBody FormacaoRequestDTO requestDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Candidato candidato = candidatoRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow { new RuntimeException("Candidato não encontrado") }

        FormacaoResponseDTO responseDTO = atualizarFormacaoUseCase.executar(id, requestDTO, candidato.getId())
        return ResponseEntity.ok(ApiResponseDTO.sucesso(responseDTO, "Formação atualizada com sucesso"))
    }

    @Operation(
            summary = "Remove uma formação acadêmica",
            description = "Deleta uma formação acadêmica pertencente ao candidato autenticado."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Formação deletada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Formação Deletada",
                                    value = """{
                                        "codigo": 200,
                                        "mensagem": "Formação deletada com sucesso",
                                        "tipo": "Sucesso"
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Violação de propriedade da formação",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Formação de Outro Candidato",
                                    value = """{
                                        "codigo": 400,
                                        "mensagem": "Você não tem permissão para deletar esta formação.",
                                        "tipo": "Requisição Inválida"
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Não Autenticado",
                                    value = """{
                                        "codigo": 401,
                                        "mensagem": "Você não está autenticado. Informe um token JWT válido.",
                                        "tipo": "Não Autorizado"
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso restrito a usuários com papel CANDIDATO",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Acesso Negado",
                                    value = """{
                                        "codigo": 403,
                                        "mensagem": "Você não tem permissão para acessar este recurso.",
                                        "tipo": "Acesso Negado"
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Formação não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Formação Não Encontrada",
                                    value = """{
                                        "codigo": 404,
                                        "mensagem": "Formação não encontrada para o ID: 1",
                                        "tipo": "Recurso Não Encontrado"
                                    }"""
                            )
                    )
            )
    ])
    @PreAuthorize("hasRole('CANDIDATO')")
    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponseDTO<Void>> deletarFormacao(
            @Parameter(description = "ID da formação acadêmica", example = "1") @PathVariable("id") Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Candidato candidato = candidatoRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow { new RuntimeException("Candidato não encontrado") }

        deletarFormacaoUseCase.executar(id, candidato.getId())
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Formação deletada com sucesso", HttpStatus.OK.value()))
    }

}
