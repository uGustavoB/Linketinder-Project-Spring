package org.ugustavodev.msnucleo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.dto.ApiResponseDTO
import org.ugustavodev.msnucleo.usecase.interfaces.IObterPerfilUseCase

@RestController
@RequestMapping("/api/perfil")
@Tag(name = "2. Perfil", description = "Consulta dos dados cadastrais completos do usuário autenticado (Candidato ou Empresa)")
class PerfilController {

    private final IObterPerfilUseCase obterPerfilUseCase

    PerfilController(IObterPerfilUseCase obterPerfilUseCase) {
        this.obterPerfilUseCase = obterPerfilUseCase
    }

    @Operation(
            summary = "Obtém o perfil completo do usuário autenticado",
            description = "Retorna `PerfilCandidatoDTO` caso o usuário autenticado possua `ROLE_CANDIDATO`, ou `PerfilEmpresaDTO` caso possua `ROLE_EMPRESA`."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Perfil obtido com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = [
                                    @ExampleObject(
                                            name = "Perfil de Candidato",
                                            summary = "Retorno quando o usuário autenticado é CANDIDATO",
                                            value = """{
                                                "codigo": 200,
                                                "mensagem": "Perfil obtido com sucesso",
                                                "tipo": "Sucesso",
                                                "dados": {
                                                    "id": 1,
                                                    "tipoUsuario": "CANDIDATO",
                                                    "nome": "João",
                                                    "sobrenome": "Silva",
                                                    "dataNascimento": "1998-05-15",
                                                    "email": "joao.silva@email.com",
                                                    "cpf": "12345678901",
                                                    "pais": "Brasil",
                                                    "estado": "SP",
                                                    "cep": "01001000",
                                                    "descricao": "Desenvolvedor Backend com foco em Java, Groovy e Spring Boot."
                                                }
                                            }"""
                                    ),
                                    @ExampleObject(
                                            name = "Perfil de Empresa",
                                            summary = "Retorno quando o usuário autenticado é EMPRESA",
                                            value = """{
                                                "codigo": 200,
                                                "mensagem": "Perfil obtido com sucesso",
                                                "tipo": "Sucesso",
                                                "dados": {
                                                    "id": 1,
                                                    "tipoUsuario": "EMPRESA",
                                                    "nome": "Tech Solutions Ltda",
                                                    "cnpj": "12345678000199",
                                                    "email": "rh@techsolutions.com.br",
                                                    "pais": "Brasil",
                                                    "estado": "SP",
                                                    "cep": "04538132",
                                                    "descricao": "Consultoria especializada em arquitetura de microsserviços e soluções em nuvem."
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
                                        "mensagem": "Perfil não encontrado",
                                        "tipo": "Recurso Não Encontrado"
                                    }"""
                            )
                    )
            )
    ])
    @GetMapping
    ResponseEntity<ApiResponseDTO<Object>> getPerfil() {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal()

        Object dto = obterPerfilUseCase.executar(usuario)
        
        if (dto != null) {
            return ResponseEntity.ok(ApiResponseDTO.sucesso(dto, "Perfil obtido com sucesso"))
        }

        throw new NoSuchElementException("Perfil não encontrado")
    }

}
