package org.ugustavodev.mscompetencia.controller

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
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.ugustavodev.mscompetencia.dto.ApiResponseDTO
import org.ugustavodev.mscompetencia.dto.request.VincularCompetenciaVagaRequestDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVagaVinculoResponseDTO
import org.ugustavodev.mscompetencia.usecase.interfaces.IDesvincularCompetenciaVagaUseCase
import org.ugustavodev.mscompetencia.usecase.interfaces.IListarCompetenciasVagaUseCase
import org.ugustavodev.mscompetencia.usecase.interfaces.IVincularCompetenciaVagaUseCase

@RestController
@RequestMapping("/api/competencias/vaga")
@Tag(name = "4. Competências da Vaga", description = "Vinculação em lote, listagem e remoção de competências exigidas em vagas de emprego")
class CompetenciaVagaController {

    private final IVincularCompetenciaVagaUseCase vincularUseCase
    private final IListarCompetenciasVagaUseCase listarUseCase
    private final IDesvincularCompetenciaVagaUseCase desvincularUseCase

    CompetenciaVagaController(
            IVincularCompetenciaVagaUseCase vincularUseCase,
            IListarCompetenciasVagaUseCase listarUseCase,
            IDesvincularCompetenciaVagaUseCase desvincularUseCase) {
        this.vincularUseCase = vincularUseCase
        this.listarUseCase = listarUseCase
        this.desvincularUseCase = desvincularUseCase
    }

    @Operation(
            summary = "Vincula competências exigidas a uma vaga de emprego",
            description = "Associa uma lista de competências a uma vaga de emprego (`vagaId`). Acessível apenas por usuários com papel `ROLE_EMPRESA`. Competências que já estejam vinculadas à vaga são ignoradas."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "201",
                    description = "Competências vinculadas à vaga com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Competências Vinculadas à Vaga",
                                    value = """{
                                        "codigo": 201,
                                        "status": 201,
                                        "mensagem": "Competências vinculadas à vaga com sucesso",
                                        "tipo": "Sucesso",
                                        "dados": [
                                            {
                                                "idVinculo": 25,
                                                "competencia": {
                                                    "id": 1,
                                                    "nome": "Java"
                                                }
                                            },
                                            {
                                                "idVinculo": 26,
                                                "competencia": {
                                                    "id": 2,
                                                    "nome": "Spring Boot"
                                                }
                                            }
                                        ]
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
                    description = "Acesso restrito a usuários com papel EMPRESA",
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
                    responseCode = "404",
                    description = "Competência informada não encontrada no catálogo global",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Competência Não Encontrada",
                                    value = """{
                                        "codigo": 404,
                                        "status": 404,
                                        "mensagem": "Competência genérica com ID 99 não encontrada.",
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
                                        "status": 422,
                                        "mensagem": "Verifique os campos e tente novamente.",
                                        "tipo": "Erro de Validação",
                                        "erros": [
                                            {
                                                "campo": "competenciaId",
                                                "mensagem": "O ID da competência é obrigatório"
                                            }
                                        ]
                                    }"""
                            )
                    )
            )
    ])
    @PostMapping("/{vagaId}")
    @PreAuthorize("hasRole('EMPRESA')")
    ResponseEntity<ApiResponseDTO<List<CompetenciaVagaVinculoResponseDTO>>> vincularCompetencia(
            @Parameter(description = "ID da vaga de emprego", example = "5") @PathVariable("vagaId") Long vagaId,
            @Valid @RequestBody List<VincularCompetenciaVagaRequestDTO> requestDTOs
    ) {
        List<CompetenciaVagaVinculoResponseDTO> responseDTOs = vincularUseCase.executar(vagaId, requestDTOs)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.sucesso(responseDTOs, "Competências vinculadas à vaga com sucesso", HttpStatus.CREATED.value()))
    }

    @Operation(
            summary = "Lista as competências exigidas por uma vaga de emprego",
            description = "Retorna todas as competências vinculadas à vaga de emprego informada (`vagaId`)."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de competências da vaga retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Competências da Vaga",
                                    value = """{
                                        "codigo": 200,
                                        "status": 200,
                                        "mensagem": "Operação realizada com sucesso",
                                        "tipo": "Sucesso",
                                        "dados": [
                                            {
                                                "idVinculo": 25,
                                                "competencia": {
                                                    "id": 1,
                                                    "nome": "Java"
                                                }
                                            }
                                        ]
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
            )
    ])
    @GetMapping("/{vagaId}")
    ResponseEntity<ApiResponseDTO<List<CompetenciaVagaVinculoResponseDTO>>> listarCompetencias(
            @Parameter(description = "ID da vaga de emprego", example = "5") @PathVariable("vagaId") Long vagaId
    ) {
        List<CompetenciaVagaVinculoResponseDTO> lista = listarUseCase.executar(vagaId)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(lista))
    }

    @Operation(
            summary = "Remove o vínculo de uma competência de uma vaga de emprego",
            description = "Desvincula uma competência de uma vaga de emprego a partir do ID do vínculo (`vinculoId`). Acessível apenas por usuários com papel `ROLE_EMPRESA`."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Vínculo removido com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Vínculo Removido",
                                    value = """{
                                        "codigo": 200,
                                        "status": 200,
                                        "mensagem": "Vínculo removido com sucesso",
                                        "tipo": "Sucesso"
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
                    description = "Acesso restrito a usuários com papel EMPRESA",
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
                    responseCode = "404",
                    description = "Vínculo não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Vínculo Não Encontrado",
                                    value = """{
                                        "codigo": 404,
                                        "status": 404,
                                        "mensagem": "Vínculo não encontrado.",
                                        "tipo": "Recurso Não Encontrado"
                                    }"""
                            )
                    )
            )
    ])
    @DeleteMapping("/vinculo/{vinculoId}")
    @PreAuthorize("hasRole('EMPRESA')")
    ResponseEntity<ApiResponseDTO<Void>> desvincularCompetencia(
            @Parameter(description = "ID único do vínculo vaga-competência", example = "25") @PathVariable("vinculoId") Long vinculoId
    ) {
        desvincularUseCase.executar(vinculoId)
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Vínculo removido com sucesso", HttpStatus.OK.value()))
    }

}
