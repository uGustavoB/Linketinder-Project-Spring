package org.ugustavodev.msnucleo.dto.response

import groovy.transform.Canonical

@Canonical
class LoginResponseDTO {
    Long id
    String tipoUsuario
    String nome
    String sobrenome
    String email
}
