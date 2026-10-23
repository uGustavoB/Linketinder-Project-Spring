package org.ugustavodev.msnucleo.dto.response

import groovy.transform.Canonical
import io.swagger.v3.oas.annotations.media.Schema

@Canonical
@Schema(description = "Dados de identificação retornados após login bem-sucedido (o token JWT é enviado em paralelo via cookie HTTP-only auth_token)")
class LoginResponseDTO {
    @Schema(description = "ID do perfil (Candidato ou Empresa)", example = "1")
    Long id

    @Schema(description = "Papel do usuário autenticado (CANDIDATO ou EMPRESA)", example = "CANDIDATO")
    String tipoUsuario

    @Schema(description = "Nome do candidato ou da empresa", example = "João")
    String nome

    @Schema(description = "Sobrenome do candidato (nulo quando tipoUsuario for EMPRESA)", example = "Silva")
    String sobrenome

    @Schema(description = "E-mail autenticado", example = "joao.silva@email.com")
    String email
}
