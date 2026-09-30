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
        var usuarioOpt = repositorio.findByEmail(email)
        if (usuarioOpt.isEmpty()) {
            throw new UsernameNotFoundException("Usuário não encontrado com o email: " + email)
        }
        return usuarioOpt.get()
    }
}
