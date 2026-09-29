package org.ugustavodev.msnucleo.controller

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

@RestController
@RequestMapping("/api/auth")
class AuthenticationController {

    private final AuthenticationManager gerenciadorAutenticacao
    private final TokenService servicoToken

    AuthenticationController(AuthenticationManager gerenciadorAutenticacao, TokenService servicoToken) {
        this.gerenciadorAutenticacao = gerenciadorAutenticacao
        this.servicoToken = servicoToken
    }

    @PostMapping("/login")
    ResponseEntity<ApiResponseDTO<LoginResponseDTO>> login(@RequestBody @Valid AuthenticationDTO dados) {
        UsernamePasswordAuthenticationToken tokenAutenticacao = new UsernamePasswordAuthenticationToken(dados.email, dados.senha)
        Authentication autenticacao = gerenciadorAutenticacao.authenticate(tokenAutenticacao)

        String tokenJWT = servicoToken.gerarToken((Usuario) autenticacao.getPrincipal())

        ResponseCookie cookie = ResponseCookie.from("auth_token", tokenJWT)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(3600)
                .build()

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponseDTO.sucesso( "Login realizado com sucesso"))
    }
}

