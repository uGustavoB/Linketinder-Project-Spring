package org.ugustavodev.msnucleo.usecase.impl


import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.request.CandidatoRequestDTO
import org.ugustavodev.msnucleo.dto.response.CandidatoResponseDTO
import org.ugustavodev.msnucleo.mapper.CandidatoMapper
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.repositories.UsuarioRepository
import org.ugustavodev.msnucleo.usecase.interfaces.ICadastrarCandidatoUseCase

import org.springframework.security.crypto.password.PasswordEncoder

@Service
class CadastrarCandidatoUseCase implements ICadastrarCandidatoUseCase{
    private final CandidatoRepository candidatoRepository
    private final PasswordEncoder codificadorSenha
    private final CandidatoMapper candidatoMapper
    private final UsuarioRepository usuarioRepository

    CadastrarCandidatoUseCase (CandidatoRepository candidatoRepository, PasswordEncoder codificadorSenha, CandidatoMapper candidatoMapper, UsuarioRepository usuarioRepository) {
        this.candidatoRepository = candidatoRepository
        this.codificadorSenha = codificadorSenha
        this.candidatoMapper = candidatoMapper
        this.usuarioRepository = usuarioRepository
    }

    @Override
    CandidatoResponseDTO executar(CandidatoRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("O objeto CandidatoRequestDTO não pode ser nulo.")
        }

        if (!requestDTO.senha || requestDTO.senha.trim().length() < 6) {
            throw new IllegalArgumentException("A senha é obrigatória e deve ter no mínimo 6 caracteres.")
        }

        if (usuarioRepository.findByEmail(requestDTO.email).isPresent()) {
            throw new IllegalArgumentException("Já existe um usuário cadastrado com o email informado")
        }

        if (candidatoRepository.findByCpf(requestDTO.cpf).isPresent()) {
            throw new IllegalArgumentException("Já existe um candidato cadastrado com o CPF informado")
        }

        Candidato candidato = candidatoMapper.fromRequestDTOToEntity(requestDTO)
        candidato.usuario.senha = codificadorSenha.encode(candidato.usuario.senha)
        
        Candidato savedCandidato = candidatoRepository.save(candidato)
        return candidatoMapper.toResponseDTO(savedCandidato)
    }

}
