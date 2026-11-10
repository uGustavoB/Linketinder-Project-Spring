package org.ugustavodev.msnucleo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.dto.ApiResponseDTO
import org.ugustavodev.msnucleo.usecase.interfaces.IObterDashboardUseCase

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "7. Dashboard", description = "Indicadores consolidados de vagas, curtidas, matches e estatísticas de competências do usuário autenticado")
class DashboardController {

    private final IObterDashboardUseCase obterDashboardUseCase

    DashboardController(IObterDashboardUseCase obterDashboardUseCase) {
        this.obterDashboardUseCase = obterDashboardUseCase
    }

    @Operation(
            summary = "Obtém os indicadores consolidados do Dashboard do usuário autenticado",
            description = "Retorna `DashboardCandidatoResponseDTO` para `ROLE_CANDIDATO`, ou `DashboardEmpresaResponseDTO` (com gráfico de competências) para `ROLE_EMPRESA`."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Dados do dashboard obtidos com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = [
                                    @ExampleObject(
                                            name = "Dashboard de Candidato",
                                            summary = "Retorno quando o usuário autenticado é CANDIDATO",
                                            value = """{
                                                "codigo": 200,
                                                "mensagem": "Dados do dashboard obtidos com sucesso",
                                                "tipo": "Sucesso",
                                                "dados": {
                                                    "tipoUsuario": "CANDIDATO",
                                                    "totalVagasSugeridas": 14,
                                                    "totalCurtidasEnviadas": 8,
                                                    "totalCurtidasRecebidas": 5,
                                                    "totalMatches": 2,
                                                    "totalCompetencias": 6
                                                }
                                            }"""
                                    ),
                                    @ExampleObject(
                                            name = "Dashboard de Empresa",
                                            summary = "Retorno quando o usuário autenticado é EMPRESA",
                                            value = """{
                                                "codigo": 200,
                                                "mensagem": "Dados do dashboard obtidos com sucesso",
                                                "tipo": "Sucesso",
                                                "dados": {
                                                    "tipoUsuario": "EMPRESA",
                                                    "totalVagasAtivas": 4,
                                                    "totalCurtidasRecebidas": 12,
                                                    "totalCurtidasEnviadas": 5,
                                                    "totalMatches": 3,
                                                    "totalCompetencias": 5,
                                                    "candidatosPorCompetencia": [
                                                        {
                                                            "competenciaId": 1,
                                                            "nomeCompetencia": "Java",
                                                            "totalCandidatos": 12
                                                        },
                                                        {
                                                            "competenciaId": 2,
                                                            "nomeCompetencia": "Spring Boot",
                                                            "totalCandidatos": 9
                                                        }
                                                    ]
                                                }
                                            }"""
                                    )
                            ]
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
                    responseCode = "404",
                    description = "Perfil não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Perfil Não Encontrado",
                                    value = """{
                                        "codigo": 404,
                                        "mensagem": "Perfil não encontrado.",
                                        "tipo": "Recurso Não Encontrado"
                                    }"""
                            )
                    )
            )
    ])
    @GetMapping
    ResponseEntity<ApiResponseDTO<Object>> getDashboard() {
        Object principal = SecurityContextHolder.getContext().getAuthentication()?.getPrincipal()
        if (!(principal instanceof Usuario)) {
            throw new AccessDeniedException("Usuário não autenticado.")
        }
        Usuario usuario = (Usuario) principal

        Object dto = obterDashboardUseCase.executar(usuario)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(dto, "Dados do dashboard obtidos com sucesso"))
    }

}
