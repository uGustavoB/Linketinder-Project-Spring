package org.ugustavodev.msnucleo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.dto.request.AuthenticationDTO
import org.ugustavodev.msnucleo.dto.response.LoginResponseDTO
import org.ugustavodev.msnucleo.security.TokenService
import org.ugustavodev.msnucleo.dto.ApiResponseDTO

import org.ugustavodev.msnucleo.usecase.interfaces.IObterDadosLoginUseCase

@RestController
@RequestMapping("/api/auth")
@Tag(name = "1. Autenticação", description = "Endpoints para login e encerramento de sessão stateless via JWT e Cookie HTTP-only (auth_token)")
class AuthenticationController {

    private final AuthenticationManager gerenciadorAutenticacao
    private final TokenService servicoToken
    private final IObterDadosLoginUseCase obterDadosLoginUseCase

    AuthenticationController(AuthenticationManager gerenciadorAutenticacao, TokenService servicoToken, IObterDadosLoginUseCase obterDadosLoginUseCase) {
        this.gerenciadorAutenticacao = gerenciadorAutenticacao
        this.servicoToken = servicoToken
        this.obterDadosLoginUseCase = obterDadosLoginUseCase
    }

    @Operation(
            summary = "Autentica usuário (Candidato ou Empresa)",
            description = "Valida as credenciais informadas, retorna os dados básicos do perfil logado e define o token JWT no cookie HTTP-only `auth_token`."
    )
    @SecurityRequirements
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Login realizado com sucesso e cookie auth_token definido",
                    content = @Content(
                            mediaType = "application/json",
                            examples = [
                                    @ExampleObject(
                                            name = "Login de Candidato",
                                            summary = "Retorno ao autenticar um candidato",
                                            value = """{
                                                "codigo": 200,
                                                "mensagem": "Login realizado com sucesso",
                                                "tipo": "Sucesso",
                                                "dados": {
                                                    "id": 1,
                                                    "tipoUsuario": "CANDIDATO",
                                                    "nome": "João",
                                                    "sobrenome": "Silva",
                                                    "email": "joao.silva@email.com"
                                                }
                                            }"""
                                    ),
                                    @ExampleObject(
                                            name = "Login de Empresaa",
                                            summary = "Retorno ao autenticar uma empresaaaa",
                                            value = """{
                                                "codigo": 200,
                                                "mensagem": "Login realizado com sucesso",
                                                "tipo": "Sucesso",
                                                "dados": {
                                                    "id": 1,
                                                    "tipoUsuario": "EMPRESA",
                                                    "nome": "Tech Solutions Ltda",
                                                    "sobrenome": null,
                                                    "email": "rh@techsolutions.com.br"
                                                }
                                            }"""
                                    )
                            ]
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciais inválidas ou usuário não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Credenciais Inválidas",
                                    value = """{
                                        "codigo": 401,
                                        "mensagem": "Credenciais inválidas ou usuário não encontrado.",
                                        "tipo": "Não Autorizado"
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
                                                "campo": "email",
                                                "mensagem": "Formato de email inválido"
                                            },
                                            {
                                                "campo": "senha",
                                                "mensagem": "O campo senha é obrigatório"
                                            }
                                        ]
                                    }"""
                            )
                    )
            )
    ])
    @PostMapping("/login")
    ResponseEntity<ApiResponseDTO<LoginResponseDTO>> login(@RequestBody @Valid AuthenticationDTO dados) {
        UsernamePasswordAuthenticationToken tokenAutenticacao = new UsernamePasswordAuthenticationToken(dados.email, dados.senha)
        Authentication autenticacao = gerenciadorAutenticacao.authenticate(tokenAutenticacao)

        Usuario usuario = (Usuario) autenticacao.getPrincipal()
        String tokenJWT = servicoToken.gerarToken(usuario)

        ResponseCookie cookie = ResponseCookie.from("auth_token", tokenJWT)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(3600)
                .build()

        LoginResponseDTO loginResponseDTO = obterDadosLoginUseCase.executar(usuario)

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponseDTO.sucesso(loginResponseDTO, "Login realizado com sucesso"))
    }

    @Operation(
            summary = "Encerra a sessão do usuário autenticado",
            description = "Remove o cookie HTTP-only `auth_token` definindo seu tempo de expiração (`maxAge`) como zero."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Logout realizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Logout com Sucesso",
                                    value = """{
                                        "codigo": 200,
                                        "mensagem": "Logout realizado com sucesso",
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
                                        "mensagem": "Você não está autenticado. Informe um token JWT válido.",
                                        "tipo": "Não Autorizado"
                                    }"""
                            )
                    )
            )
    ])
    @PostMapping("/logout")
    ResponseEntity<ApiResponseDTO<String>> logout() {
        ResponseCookie cookie = ResponseCookie.from("auth_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build()

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponseDTO.sucesso("Logout realizado com sucesso"))
    }
}
