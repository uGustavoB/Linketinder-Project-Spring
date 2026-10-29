package org.ugustavodev.mscompetencia.security

import groovy.transform.Canonical

@Canonical
class UsuarioAutenticado {

    String email
    String role
    Long perfilId

}
