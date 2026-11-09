package org.ugustavodev.msnucleo.security

import org.springframework.core.MethodParameter
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.dto.ApiResponseDTO
import org.ugustavodev.msnucleo.dto.response.CandidatoResponseDTO
import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO
import org.ugustavodev.msnucleo.repositories.UsuarioRepository

@ControllerAdvice
class RenovarSessaoAdvice implements ResponseBodyAdvice<Object> {

    private final TokenService tokenService
    private final UsuarioRepository usuarioRepository

    RenovarSessaoAdvice(TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.tokenService = tokenService
        this.usuarioRepository = usuarioRepository
    }

    @Override
    boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.hasMethodAnnotation(RenovarSessao)
    }

    @Override
    Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        if (body instanceof ApiResponseDTO) {
            ApiResponseDTO<?> apiResponse = (ApiResponseDTO<?>) body
            Object dados = apiResponse.dados

            String email = null
            Long perfilId = null

            if (dados instanceof CandidatoResponseDTO) {
                CandidatoResponseDTO dto = (CandidatoResponseDTO) dados
                email = dto.email()
                perfilId = dto.id()
            } else if (dados instanceof EmpresaResponseDTO) {
                EmpresaResponseDTO dto = (EmpresaResponseDTO) dados
                email = dto.email()
                perfilId = dto.id()
            }

            if (email && perfilId) {
                Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email)
                if (usuarioOpt.isPresent()) {
                    String novoToken = tokenService.gerarToken(usuarioOpt.get(), perfilId)
                    ResponseCookie cookie = ResponseCookie.from("auth_token", novoToken)
                            .httpOnly(true)
                            .secure(false)
                            .path("/")
                            .maxAge(3600)
                            .build()
                    response.headers.add(HttpHeaders.SET_COOKIE, cookie.toString())
                }
            }
        }
        return body
    }

}
