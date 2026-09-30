package org.ugustavodev.msnucleo.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.ugustavodev.msnucleo.repositories.UsuarioRepository

@Component
class SecurityFilter extends OncePerRequestFilter {

    private final TokenService servicoToken
    private final UsuarioRepository repositorioUsuario

    SecurityFilter(TokenService servicoToken, UsuarioRepository repositorioUsuario) {
        this.servicoToken = servicoToken
        this.repositorioUsuario = repositorioUsuario
    }

    @Override
    protected void doFilterInternal(HttpServletRequest requisicao, HttpServletResponse resposta, FilterChain cadeiaFiltros) throws ServletException, IOException {
        String token = recuperarToken(requisicao)
        if (token != null) {
            String email = servicoToken.validarToken(token)
            if (email != null && !email.isEmpty()) {
                var usuarioOpt = repositorioUsuario.findByEmail(email)
                if (usuarioOpt.isPresent()) {
                    UserDetails usuario = usuarioOpt.get()
                    UsernamePasswordAuthenticationToken autenticacao = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities())
                    SecurityContextHolder.getContext().setAuthentication(autenticacao)
                }
            }
        }
        cadeiaFiltros.doFilter(requisicao, resposta)
    }

    private String recuperarToken(HttpServletRequest requisicao) {
        if (requisicao.getCookies() != null) {
            for (var cookie in requisicao.getCookies()) {
                if (cookie.getName() == "auth_token") {
                    return cookie.getValue()
                }
            }
        }

        String cabecalhoAutenticacao = requisicao.getHeader("Authorization")
        if (cabecalhoAutenticacao == null || !cabecalhoAutenticacao.startsWith("Bearer ")) {
            return null
        }
        return cabecalhoAutenticacao.replace("Bearer ", "")
    }
}
