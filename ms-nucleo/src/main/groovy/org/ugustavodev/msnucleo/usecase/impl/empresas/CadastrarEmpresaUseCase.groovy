package org.ugustavodev.msnucleo.usecase.impl.empresas

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.dto.request.EmpresaRequestDTO
import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO
import org.ugustavodev.msnucleo.mapper.EmpresaMapper
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.repositories.UsuarioRepository
import org.ugustavodev.msnucleo.usecase.interfaces.empresas.ICadastrarEmpresaUseCase

import org.springframework.security.crypto.password.PasswordEncoder

@Service
class CadastrarEmpresaUseCase implements ICadastrarEmpresaUseCase {

    private final EmpresaRepository empresaRepository
    private final PasswordEncoder passwordEncoder
    private final EmpresaMapper empresaMapper
    private final UsuarioRepository usuarioRepository

    CadastrarEmpresaUseCase(EmpresaRepository empresaRepository, PasswordEncoder passwordEncoder, EmpresaMapper empresaMapper, UsuarioRepository usuarioRepository) {
        this.empresaRepository = empresaRepository
        this.passwordEncoder = passwordEncoder
        this.empresaMapper = empresaMapper
        this.usuarioRepository = usuarioRepository
    }

    @Override
    EmpresaResponseDTO executar(EmpresaRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("O objeto EmpresaRequestDTO não pode ser nulo.")
        }

        if (!requestDTO.senha || requestDTO.senha.trim().length() < 6) {
            throw new IllegalArgumentException("A senha é obrigatória e deve ter no mínimo 6 caracteres.")
        }

        if (usuarioRepository.findByEmail(requestDTO.email).isPresent()) {
            throw new IllegalArgumentException("Já existe um usuário cadastrado com o email informado")
        }

        if (empresaRepository.findByCnpj(requestDTO.cnpj).isPresent()) {
            throw new IllegalArgumentException("Já existe uma empresa cadastrada com o CNPJ informado")
        }

        Empresa empresa = empresaMapper.fromRequestDTOToEntity(requestDTO)
        empresa.usuario.senha = passwordEncoder.encode(empresa.usuario.senha)

        Empresa savedEmpresa = empresaRepository.save(empresa)
        return empresaMapper.toResponseDTO(savedEmpresa)
    }

}
