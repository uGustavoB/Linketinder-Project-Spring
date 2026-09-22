package org.ugustavodev.msnucleo.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.repository.UsuarioRepository

@Service
class AuthorizationService implements UserDetailsService {

    private final UsuarioRepository repositorio

    AuthorizationService(UsuarioRepository repositorio) {
        this.repositorio = repositorio
    }

    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repositorio.findByEmail(email)
    }
}
