package org.ugustavodev.msnucleo.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.domain.Usuario

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class TokenService {

    @Value('${api.security.token.secret}')
    String chaveSecreta

    String gerarToken(Usuario usuario) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(chaveSecreta)

            return JWT.create()
                    .withIssuer("ms-nucleo")
                    .withSubject(usuario.email)
                    .withClaim("role", usuario.role.name())
                    .withExpiresAt(gerarDataExpiracao())
                    .sign(algoritmo)
        } catch (JWTCreationException excecao) {
            throw new RuntimeException("Erro ao gerar token", excecao)
        }
    }

    String validarToken(String token) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(chaveSecreta)
            return JWT.require(algoritmo)
                    .withIssuer("ms-nucleo")
                    .build()
                    .verify(token)
                    .getSubject()
        } catch (JWTVerificationException excecao) {
            System.err.println("Erro ao validar token: " + excecao.getMessage())
            return ""
        }
    }

    private static Instant gerarDataExpiracao() {
        return LocalDateTime.now().plusHours(24).toInstant(ZoneOffset.of("-03:00"))
    }
}
