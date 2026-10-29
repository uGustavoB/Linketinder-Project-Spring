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
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.ugustavodev.mscompetencia.dto.ApiResponseDTO
import org.ugustavodev.mscompetencia.dto.request.VincularCompetenciaRequestDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVinculoResponseDTO
import org.ugustavodev.mscompetencia.security.UsuarioAutenticado
import org.ugustavodev.mscompetencia.usecase.interfaces.IDesvincularCompetenciaCandidatoUseCase
import org.ugustavodev.mscompetencia.usecase.interfaces.IListarCompetenciasCandidatoUseCase
import org.ugustavodev.mscompetencia.usecase.interfaces.IVincularCompetenciaCandidatoUseCase

@RestController
@RequestMapping("/api/competencias/candidato")
@Tag(name = "2. Competências do Candidato", description = "Vinculação em lote, listagem e remoção de competências com nível de proficiência do perfil de candidatos")
class CompetenciaCandidatoController {

    private final IVincularCompetenciaCandidatoUseCase vincularUseCase
    private final IListarCompetenciasCandidatoUseCase listarUseCase
    private final IDesvincularCompetenciaCandidatoUseCase desvincularUseCase

    CompetenciaCandidatoController(
            IVincularCompetenciaCandidatoUseCase vincularUseCase,
            IListarCompetenciasCandidatoUseCase listarUseCase,
            IDesvincularCompetenciaCandidatoUseCase desvincularUseCase) {
        this.vincularUseCase = vincularUseCase
        this.listarUseCase = listarUseCase
        this.desvincularUseCase = desvincularUseCase
    }

    @Operation(
            summary = "Vincula competências ao perfil de um candidato",
            description = "Associa uma lista de competências e seus respectivos níveis (`BASICO`, `MEDIO`, `AVANCADO`) ao candidato autenticado (`ROLE_CANDIDATO`). Caso a competência já esteja vinculada ao candidato, seu nível de proficiência é atualizado."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "201",
                    description = "Competências vinculadas ao candidato com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Competências Vinculadas ao Candidato",
                                    value = """{
                                        "codigo": 201,
                                        "status": 201,
                                        "mensagem": "Competências vinculadas ao candidato com sucesso",
                                        "tipo": "Sucesso",
                                        "dados": [
                                            {
                                                "idVinculo": 10,
                                                "competencia": {
                                                    "id": 1,
                                                    "nome": "Java"
                                                },
                                                "nivel": "AVANCADO"
                                            },
                                            {
                                                "idVinculo": 11,
                                                "competencia": {
                                                    "id": 2,
                                                    "nome": "Spring Boot"
                                                },
                                                "nivel": "MEDIO"
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
                    description = "Acesso restrito ao próprio candidato autenticado (ROLE_CANDIDATO)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Acesso Negado ou Outro Candidato",
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
                                        "mensagem": "Competência com ID 99 não encontrada.",
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
                                            },
                                            {
                                                "campo": "nivel",
                                                "mensagem": "O nível da competência é obrigatório"
                                            }
                                        ]
                                    }"""
                            )
                    )
            )
    ])
    @PostMapping("/{candidatoId}")
    @PreAuthorize("hasRole('CANDIDATO')")
    ResponseEntity<ApiResponseDTO<List<CompetenciaVinculoResponseDTO>>> vincularCompetencia(
            @Parameter(description = "ID do candidato", example = "1") @PathVariable("candidatoId") Long candidatoId,
            @Valid @RequestBody List<VincularCompetenciaRequestDTO> requestDTOs,
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    ) {
        if (usuarioAutenticado?.perfilId == null || usuarioAutenticado.perfilId != candidatoId) {
            throw new AccessDeniedException("Você não tem permissão para alterar as competências de outro candidato.")
        }

        List<CompetenciaVinculoResponseDTO> responseDTOs = vincularUseCase.executar(candidatoId, requestDTOs)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.sucesso(responseDTOs, "Competências vinculadas ao candidato com sucesso", HttpStatus.CREATED.value()))
    }

    @Operation(
            summary = "Lista as competências vinculadas a um candidato",
            description = "Retorna todas as competências e seus respectivos níveis de proficiência associados ao candidato informado."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de competências do candidato retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Competências do Candidato",
                                    value = """{
                                        "codigo": 200,
                                        "status": 200,
                                        "mensagem": "Operação realizada com sucesso",
                                        "tipo": "Sucesso",
                                        "dados": [
                                            {
                                                "idVinculo": 10,
                                                "competencia": {
                                                    "id": 1,
                                                    "nome": "Java"
                                                },
                                                "nivel": "AVANCADO"
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
    @GetMapping("/{candidatoId}")
    ResponseEntity<ApiResponseDTO<List<CompetenciaVinculoResponseDTO>>> listarCompetencias(
            @Parameter(description = "ID do candidato", example = "1") @PathVariable("candidatoId") Long candidatoId
    ) {
        List<CompetenciaVinculoResponseDTO> lista = listarUseCase.executar(candidatoId)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(lista))
    }

    @Operation(
            summary = "Remove o vínculo de uma competência do candidato",
            description = "Desvincula uma competência do perfil do candidato autenticado a partir do ID do vínculo (`vinculoId`), validando a titularidade do registro."
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
                    description = "Acesso restrito ao candidato titular do vínculo (ROLE_CANDIDATO)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Acesso Negado ou Vínculo de Outro Candidato",
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
    @PreAuthorize("hasRole('CANDIDATO')")
    ResponseEntity<ApiResponseDTO<Void>> desvincularCompetencia(
            @Parameter(description = "ID único do vínculo candidato-competência", example = "10") @PathVariable("vinculoId") Long vinculoId,
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    ) {
        desvincularUseCase.executar(vinculoId, usuarioAutenticado?.perfilId)
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Vínculo removido com sucesso", HttpStatus.OK.value()))
    }

}
