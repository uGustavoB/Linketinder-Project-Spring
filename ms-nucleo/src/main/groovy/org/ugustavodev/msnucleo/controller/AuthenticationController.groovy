package org.ugustavodev.msnucleo.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
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

        LoginResponseDTO resposta = new LoginResponseDTO(tokenJWT)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(resposta, "Login realizado com sucesso"))
    }
}

