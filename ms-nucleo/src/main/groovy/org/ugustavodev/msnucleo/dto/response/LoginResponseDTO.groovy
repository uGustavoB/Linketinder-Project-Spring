package org.ugustavodev.msnucleo.dto.response

import groovy.transform.Canonical

@Canonical
class LoginResponseDTO {
    String tipoUsuario
    String nome
    String sobrenome
    String email
}
