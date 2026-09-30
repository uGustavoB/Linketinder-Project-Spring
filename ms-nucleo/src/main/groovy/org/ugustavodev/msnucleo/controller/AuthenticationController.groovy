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

import org.ugustavodev.msnucleo.usecase.interfaces.IObterDadosLoginUseCase

@RestController
@RequestMapping("/api/auth")
class AuthenticationController {

    private final AuthenticationManager gerenciadorAutenticacao
    private final TokenService servicoToken
    private final IObterDadosLoginUseCase obterDadosLoginUseCase

    AuthenticationController(AuthenticationManager gerenciadorAutenticacao, TokenService servicoToken, IObterDadosLoginUseCase obterDadosLoginUseCase) {
        this.gerenciadorAutenticacao = gerenciadorAutenticacao
        this.servicoToken = servicoToken
        this.obterDadosLoginUseCase = obterDadosLoginUseCase
    }

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

