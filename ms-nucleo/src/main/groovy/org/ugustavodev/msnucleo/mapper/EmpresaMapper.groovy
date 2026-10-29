package org.ugustavodev.msnucleo.mapper

import org.springframework.stereotype.Component
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.dto.request.EmpresaRequestDTO
import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO
import org.ugustavodev.msnucleo.dto.response.EmpresaPublicoResponseDTO
import org.ugustavodev.msnucleo.model.Empresa

@Component
class EmpresaMapper {

    EmpresaResponseDTO toResponseDTO(Empresa empresa) {
        return new EmpresaResponseDTO(
                empresa.id,
                empresa.nome,
                empresa.cnpj,
                empresa.usuario?.email,
                empresa.pais,
                empresa.estado,
                empresa.cep,
                empresa.descricao
        )
    }

    EmpresaPublicoResponseDTO toPublicoResponseDTO(Empresa empresa) {
        return new EmpresaPublicoResponseDTO(
                empresa.id,
                empresa.pais,
                empresa.estado,
                empresa.descricao
        )
    }

    Empresa fromResponseDTOToEntity(EmpresaResponseDTO dto) {
        Empresa empresa = new Empresa()
        empresa.id = dto.id()
        empresa.nome = dto.nome()
        empresa.cnpj = dto.cnpj()
        empresa.usuario = new Usuario(email: dto.email())
        empresa.pais = dto.pais()
        empresa.estado = dto.estado()
        empresa.cep = dto.cep()
        empresa.descricao = dto.descricao()
        return empresa
    }

    Empresa fromRequestDTOToEntity(EmpresaRequestDTO dto) {
        Empresa empresa = new Empresa()
        empresa.nome = dto.nome
        empresa.cnpj = dto.cnpj
        empresa.usuario = new Usuario(email: dto.email, senha: dto.senha, role: Role.EMPRESA)
        empresa.pais = dto.pais
        empresa.estado = dto.estado
        empresa.cep = dto.cep
        empresa.descricao = dto.descricao
        return empresa
    }

}
