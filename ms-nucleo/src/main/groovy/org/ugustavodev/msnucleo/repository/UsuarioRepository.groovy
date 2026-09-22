package org.ugustavodev.msnucleo.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.security.core.userdetails.UserDetails
import org.ugustavodev.msnucleo.domain.Usuario

interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    UserDetails findByEmail(String email)
}
