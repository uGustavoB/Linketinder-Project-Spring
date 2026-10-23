package org.ugustavodev.mscompetencia.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TokenService {
    @Value('${api.security.token.secret}')
    String chaveSecreta

    DecodedJWT validarToken(String token) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(chaveSecreta)
            return JWT.require(algoritmo)
                    .withIssuer("ms-nucleo")
                    .build()
                    .verify(token)
        } catch (JWTVerificationException excecao) {
            return null
        }
    }
}
