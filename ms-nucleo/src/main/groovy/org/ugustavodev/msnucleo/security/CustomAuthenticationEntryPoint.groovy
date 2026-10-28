package org.ugustavodev.msnucleo.security

import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8")
        response.setStatus(HttpStatus.UNAUTHORIZED.value())

        String json = """{
  "codigo": ${HttpStatus.UNAUTHORIZED.value()},
  "mensagem": "Você não está autenticado. Informe um token JWT válido.",
  "tipo": "Não Autorizado"
}"""

        response.getWriter().write(json)
    }

}
