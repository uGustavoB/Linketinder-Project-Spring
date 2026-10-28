package org.ugustavodev.msnucleo.security

import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8")
        response.setStatus(HttpStatus.FORBIDDEN.value())

        String json = """{
  "codigo": ${HttpStatus.FORBIDDEN.value()},
  "mensagem": "Você não tem permissão para acessar este recurso.",
  "tipo": "Acesso Negado"
}"""

        response.getWriter().write(json)
    }

}
