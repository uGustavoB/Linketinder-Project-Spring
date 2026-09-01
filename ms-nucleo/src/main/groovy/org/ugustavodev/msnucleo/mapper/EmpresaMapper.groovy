package org.ugustavodev.msnucleo.mapper

import org.springframework.stereotype.Component
import org.ugustavodev.msnucleo.dto.request.EmpresaRequestDTO
import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO
import org.ugustavodev.msnucleo.model.Empresa

@Component
class EmpresaMapper {

    static EmpresaResponseDTO toResponseDTO(Empresa empresa) {
        return new EmpresaResponseDTO(
                empresa.id,
                empresa.nome,
                empresa.cnpj,
                empresa.email,
                empresa.pais,
                empresa.estado,
                empresa.cep,
                empresa.descricao
        )
    }

    static Empresa fromResponseDTOToEntity(EmpresaResponseDTO dto) {
        Empresa empresa = new Empresa()
        empresa.id = dto.id()
        empresa.nome = dto.nome()
        empresa.cnpj = dto.cnpj()
        empresa.email = dto.email()
        empresa.pais = dto.pais()
        empresa.estado = dto.estado()
        empresa.cep = dto.cep()
        empresa.descricao = dto.descricao()
        return empresa
    }

    static Empresa fromRequestDTOToEntity(EmpresaRequestDTO dto) {
        Empresa empresa = new Empresa()
        empresa.nome = dto.nome
        empresa.cnpj = dto.cnpj
        empresa.email = dto.email
        empresa.pais = dto.pais
        empresa.estado = dto.estado
        empresa.cep = dto.cep
        empresa.descricao = dto.descricao
        empresa.senha = dto.senha
        return empresa
    }
}
