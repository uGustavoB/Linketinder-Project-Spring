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
import org.ugustavodev.mscompetencia.usecase.interfaces.IDesvincularCompetenciaEmpresaUseCase
import org.ugustavodev.mscompetencia.usecase.interfaces.IListarCompetenciasEmpresaUseCase
import org.ugustavodev.mscompetencia.usecase.interfaces.IVincularCompetenciaEmpresaUseCase

@RestController
@RequestMapping("/api/competencias/empresa")
@Tag(name = "3. Competências da Empresa", description = "Vinculação em lote, listagem e remoção de competências associadas ao perfil de empresas")
class CompetenciaEmpresaController {

    private final IVincularCompetenciaEmpresaUseCase vincularUseCase
    private final IListarCompetenciasEmpresaUseCase listarUseCase
    private final IDesvincularCompetenciaEmpresaUseCase desvincularUseCase

    CompetenciaEmpresaController(
            IVincularCompetenciaEmpresaUseCase vincularUseCase,
            IListarCompetenciasEmpresaUseCase listarUseCase,
            IDesvincularCompetenciaEmpresaUseCase desvincularUseCase) {
        this.vincularUseCase = vincularUseCase
        this.listarUseCase = listarUseCase
        this.desvincularUseCase = desvincularUseCase
    }

    @Operation(
            summary = "Vincula competências ao perfil de uma empresa",
            description = "Associa uma lista de competências e seus respectivos níveis (`BASICO`, `MEDIO`, `AVANCADO`) à empresa autenticada (`ROLE_EMPRESA`). Competências que já estejam vinculadas à empresa são ignoradas."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "201",
                    description = "Competências vinculadas à empresa com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Competências Vinculadas à Empresa",
                                    value = """{
                                        "codigo": 201,
                                        "status": 201,
                                        "mensagem": "Competências vinculadas à empresa com sucesso",
                                        "tipo": "Sucesso",
                                        "dados": [
                                            {
                                                "idVinculo": 15,
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
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso restrito à própria empresa autenticada (ROLE_EMPRESA)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Acesso Negado ou Outra Empresa",
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
    @PostMapping("/{empresaId}")
    @PreAuthorize("hasRole('EMPRESA')")
    ResponseEntity<ApiResponseDTO<List<CompetenciaVinculoResponseDTO>>> vincularCompetencia(
            @Parameter(description = "ID da empresa", example = "1") @PathVariable("empresaId") Long empresaId,
            @Valid @RequestBody List<VincularCompetenciaRequestDTO> requestDTOs,
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    ) {
        if (usuarioAutenticado?.perfilId == null || usuarioAutenticado.perfilId != empresaId) {
            throw new AccessDeniedException("Você não tem permissão para alterar as competências de outra empresa.")
        }

        List<CompetenciaVinculoResponseDTO> responseDTOs = vincularUseCase.executar(empresaId, requestDTOs)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.sucesso(responseDTOs, "Competências vinculadas à empresa com sucesso", HttpStatus.CREATED.value()))
    }

    @Operation(
            summary = "Lista as competências vinculadas a uma empresa",
            description = "Retorna todas as competências e seus respectivos níveis associados à empresa informada."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de competências da empresa retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Competências da Empresa",
                                    value = """{
                                        "codigo": 200,
                                        "status": 200,
                                        "mensagem": "Operação realizada com sucesso",
                                        "tipo": "Sucesso",
                                        "dados": [
                                            {
                                                "idVinculo": 15,
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
    @GetMapping("/{empresaId}")
    ResponseEntity<ApiResponseDTO<List<CompetenciaVinculoResponseDTO>>> listarCompetencias(
            @Parameter(description = "ID da empresa", example = "1") @PathVariable("empresaId") Long empresaId
    ) {
        List<CompetenciaVinculoResponseDTO> lista = listarUseCase.executar(empresaId)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(lista))
    }

    @Operation(
            summary = "Remove o vínculo de uma competência da empresa",
            description = "Desvincula uma competência do perfil da empresa autenticada a partir do ID do vínculo (`vinculoId`), validando a titularidade do registro."
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
                    description = "Acesso restrito à empresa titular do vínculo (ROLE_EMPRESA)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Acesso Negado ou Vínculo de Outra Empresa",
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
            @Parameter(description = "ID único do vínculo empresa-competência", example = "15") @PathVariable("vinculoId") Long vinculoId,
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    ) {
        desvincularUseCase.executar(vinculoId, usuarioAutenticado?.perfilId)
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Vínculo removido com sucesso", HttpStatus.OK.value()))
    }

}
