package org.ugustavodev.mscompetencia.security

import com.auth0.jwt.interfaces.DecodedJWT
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService

    SecurityFilter(TokenService tokenService) {
        this.tokenService = tokenService
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = recuperarToken(request)

        if (token != null) {
            DecodedJWT jwt = tokenService.validarToken(token)
            if (jwt != null) {
                String email = jwt.getSubject()
                String role = jwt.getClaim("role")?.asString()
                Long perfilId = jwt.getClaim("perfilId")?.asLong()

                List<SimpleGrantedAuthority> authorities = []
                if (role != null) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role))
                }

                UsuarioAutenticado principal = new UsuarioAutenticado(email, role, perfilId)

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, authorities)
                SecurityContextHolder.getContext().setAuthentication(authentication)
            }
        }

        filterChain.doFilter(request, response)
    }

    private String recuperarToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if (cookie.getName() == "auth_token") {
                    return cookie.getValue()
                }
            }
        }

        String authHeader = request.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.replace("Bearer ", "")
        }

        return null
    }

}
