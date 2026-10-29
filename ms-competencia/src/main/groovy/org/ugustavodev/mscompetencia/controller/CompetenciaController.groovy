package org.ugustavodev.mscompetencia.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.ugustavodev.mscompetencia.dto.ApiResponseDTO
import org.ugustavodev.mscompetencia.dto.request.CompetenciaRequestDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaResponseDTO
import org.ugustavodev.mscompetencia.usecase.interfaces.ICadastrarCompetenciaUseCase
import org.ugustavodev.mscompetencia.usecase.interfaces.IListarCompetenciasUseCase

@RestController
@RequestMapping("/api/competencias")
@Tag(name = "1. Catálogo de Competências", description = "Cadastro e listagem do catálogo global de competências da plataforma")
class CompetenciaController {

    private final ICadastrarCompetenciaUseCase cadastrarCompetenciaUseCase
    private final IListarCompetenciasUseCase listarCompetenciasUseCase

    CompetenciaController(
            ICadastrarCompetenciaUseCase cadastrarCompetenciaUseCase,
            IListarCompetenciasUseCase listarCompetenciasUseCase) {
        this.cadastrarCompetenciaUseCase = cadastrarCompetenciaUseCase
        this.listarCompetenciasUseCase = listarCompetenciasUseCase
    }

    @Operation(
            summary = "Cadastra uma nova competência no catálogo global",
            description = "Registra uma nova competência técnica ou comportamental no catálogo global da plataforma. Acessível por usuários autenticados com papel `ROLE_CANDIDATO` ou `ROLE_EMPRESA`."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "201",
                    description = "Competência global criada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Competência Cadastrada",
                                    value = """{
                                        "codigo": 201,
                                        "status": 201,
                                        "mensagem": "Competência global criada com sucesso",
                                        "tipo": "Sucesso",
                                        "dados": {
                                            "id": 1,
                                            "nome": "Java"
                                        }
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Competência já existente no catálogo global",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Competência Duplicada",
                                    value = """{
                                        "codigo": 400,
                                        "status": 400,
                                        "mensagem": "Já existe uma competência com este nome.",
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
                                        "status": 401,
                                        "mensagem": "Você não está autenticado. Informe um token JWT válido.",
                                        "tipo": "Não Autorizado"
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso restrito a usuários com papel CANDIDATO ou EMPRESA",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Acesso Negado",
                                    value = """{
                                        "codigo": 403,
                                        "status": 403,
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
                                        "status": 422,
                                        "mensagem": "Verifique os campos e tente novamente.",
                                        "tipo": "Erro de Validação",
                                        "erros": [
                                            {
                                                "campo": "nome",
                                                "mensagem": "O nome da competência é obrigatório"
                                            }
                                        ]
                                    }"""
                            )
                    )
            )
    ])
    @PostMapping
    @PreAuthorize("hasAnyRole('CANDIDATO', 'EMPRESA')")
    ResponseEntity<ApiResponseDTO<CompetenciaResponseDTO>> cadastrarCompetencia(@Valid @RequestBody CompetenciaRequestDTO requestDTO) {
        CompetenciaResponseDTO responseDTO = cadastrarCompetenciaUseCase.executar(requestDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.sucesso(responseDTO, "Competência global criada com sucesso", HttpStatus.CREATED.value()))
    }

    @Operation(
            summary = "Lista todas as competências do catálogo global",
            description = "Retorna a lista completa de competências disponíveis para vinculação em perfis de candidatos, empresas e vagas. Endpoint público (não requer autenticação)."
    )
    @SecurityRequirements
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de competências retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Catálogo de Competências",
                                    value = """{
                                        "codigo": 200,
                                        "status": 200,
                                        "mensagem": "Operação realizada com sucesso",
                                        "tipo": "Sucesso",
                                        "dados": [
                                            {
                                                "id": 1,
                                                "nome": "Java"
                                            },
                                            {
                                                "id": 2,
                                                "nome": "Spring Boot"
                                            },
                                            {
                                                "id": 3,
                                                "nome": "PostgreSQL"
                                            }
                                        ]
                                    }"""
                            )
                    )
            )
    ])
    @GetMapping
    ResponseEntity<ApiResponseDTO<List<CompetenciaResponseDTO>>> listarCompetencias() {
        List<CompetenciaResponseDTO> lista = listarCompetenciasUseCase.executar()
        return ResponseEntity.ok(ApiResponseDTO.sucesso(lista))
    }

}
