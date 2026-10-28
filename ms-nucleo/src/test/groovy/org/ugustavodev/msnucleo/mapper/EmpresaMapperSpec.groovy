package org.ugustavodev.msnucleo.mapper

import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.dto.request.EmpresaRequestDTO
import org.ugustavodev.msnucleo.dto.response.EmpresaPublicoResponseDTO
import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO
import org.ugustavodev.msnucleo.model.Empresa
import spock.lang.Specification
import spock.lang.Subject

class EmpresaMapperSpec extends Specification {

    @Subject
    EmpresaMapper mapper = new EmpresaMapper()

    void "Deve converter entidade Empresa para EmpresaResponseDTO completo"() {
        given: "Uma entidade Empresa preenchida"
        Empresa empresa = new Empresa(
                id: 1L,
                nome: "Tech Solutions Ltda",
                cnpj: "12345678000199",
                pais: "Brasil",
                estado: "SP",
                cep: "04538132",
                descricao: "Consultoria TI",
                usuario: new Usuario(email: "rh@techsolutions.com.br")
        )

        when: "Converte para EmpresaResponseDTO"
        EmpresaResponseDTO dto = mapper.toResponseDTO(empresa)

        then: "Todos os campos corporativos sao mapeados"
        dto.id() == 1L
        dto.nome() == "Tech Solutions Ltda"
        dto.cnpj() == "12345678000199"
        dto.email() == "rh@techsolutions.com.br"
        dto.pais() == "Brasil"
        dto.estado() == "SP"
        dto.cep() == "04538132"
        dto.descricao() == "Consultoria TI"
    }

    void "Deve converter entidade Empresa para EmpresaPublicoResponseDTO anonimizado"() {
        given: "Uma entidade Empresa preenchida"
        Empresa empresa = new Empresa(
                id: 1L,
                nome: "Tech Solutions Ltda",
                cnpj: "12345678000199",
                pais: "Brasil",
                estado: "SP",
                descricao: "Consultoria TI"
        )

        when: "Converte para EmpresaPublicoResponseDTO"
        EmpresaPublicoResponseDTO dto = mapper.toPublicoResponseDTO(empresa)

        then: "Retorna apenas os campos publicos"
        dto.id() == 1L
        dto.pais() == "Brasil"
        dto.estado() == "SP"
        dto.descricao() == "Consultoria TI"
    }

    void "Deve converter EmpresaRequestDTO para entidade Empresa com papel EMPRESA"() {
        given: "Um EmpresaRequestDTO com dados formatados"
        EmpresaRequestDTO request = new EmpresaRequestDTO(
                nome: "Tech Solutions Ltda",
                cnpj: "12.345.678/0001-99",
                email: "RH@TECHSOLUTIONS.COM.BR",
                senha: "senha123",
                pais: "Brasil",
                estado: "SP",
                cep: "04538-132",
                descricao: "Consultoria TI"
        )

        when: "Converte para entidade Empresa"
        Empresa entidade = mapper.fromRequestDTOToEntity(request)

        then: "Os dados sanitizados e o Usuario com Role.EMPRESA sao preenchidos"
        entidade.nome == "Tech Solutions Ltda"
        entidade.cnpj == "12345678000199"
        entidade.cep == "04538132"
        entidade.usuario.email == "rh@techsolutions.com.br"
        entidade.usuario.role == Role.EMPRESA
    }
}
