package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.dto.response.LoginResponseDTO
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IObterDadosLoginUseCase

@Service
class ObterDadosLoginUseCase implements IObterDadosLoginUseCase {

    private final CandidatoRepository candidatoRepository
    private final EmpresaRepository empresaRepository

    ObterDadosLoginUseCase(CandidatoRepository candidatoRepository, EmpresaRepository empresaRepository) {
        this.candidatoRepository = candidatoRepository
        this.empresaRepository = empresaRepository
    }

    @Override
    LoginResponseDTO executar(Usuario usuario) {
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO()
        loginResponseDTO.email = usuario.email
        loginResponseDTO.tipoUsuario = usuario.role.name()

        if (usuario.role == Role.CANDIDATO) {
            Optional<Candidato> candidato = candidatoRepository.findByUsuarioEmail(usuario.email)
            if (candidato.isPresent()) {
                loginResponseDTO.id = candidato.get().id
                loginResponseDTO.nome = candidato.get().nome
                loginResponseDTO.sobrenome = candidato.get().sobrenome
            }
        } else if (usuario.role == Role.EMPRESA) {
            Optional<Empresa> empresa = empresaRepository.findByUsuarioEmail(usuario.email)
            if (empresa.isPresent()) {
                loginResponseDTO.id = empresa.get().id
                loginResponseDTO.nome = empresa.get().nome
            }
        }

        return loginResponseDTO
    }
}
