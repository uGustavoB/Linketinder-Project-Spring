package org.ugustavodev.msnucleo.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.repositories.UsuarioRepository

@Service
class AuthorizationService implements UserDetailsService {

    private final UsuarioRepository repositorio

    AuthorizationService(UsuarioRepository repositorio) {
        this.repositorio = repositorio
    }

    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserDetails usuario = repositorio.findByEmail(email)
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado com o email: " + email)
        }
        return usuario
    }
}
