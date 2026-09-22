package org.ugustavodev.msnucleo.domain.enums

enum Role {
    CANDIDATO("ROLE_CANDIDATO"),
    EMPRESA("ROLE_EMPRESA")

    private String role

    Role(String role) {
        this.role = role
    }

    String getRole() {
        return role
    }
}
