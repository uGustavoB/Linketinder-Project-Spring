package org.ugustavodev.msnucleo.mapper

import org.springframework.stereotype.Component
import org.ugustavodev.msnucleo.dto.request.CandidatoRequestDTO
import org.ugustavodev.msnucleo.dto.response.CandidatoResponseDTO
import org.ugustavodev.msnucleo.model.Candidato

import java.time.LocalDate

@Component
class CandidatoMapper {

    public static CandidatoResponseDTO toResponseDTO(Candidato candidato) {
        return new CandidatoResponseDTO(
                candidato.id,
                candidato.nome,
                candidato.sobrenome,
                LocalDate.parse(candidato.dataNascimento),
                candidato.email,
                candidato.cpf,
                candidato.pais,
                candidato.estado,
                candidato.cep,
                candidato.descricao
        )
    }

    public static Candidato fromResponseDTOToEntity(CandidatoResponseDTO dto) {
        Candidato candidato = new Candidato()
        candidato.id = dto.id()
        candidato.nome = dto.nome()
        candidato.sobrenome = dto.sobrenome()
        candidato.dataNascimento = dto.dataNascimento().toString()
        candidato.email = dto.email()
        candidato.cpf = dto.cpf()
        candidato.pais = dto.pais()
        candidato.estado = dto.estado()
        candidato.cep = dto.cep()
        candidato.descricao = dto.descricao()
        return candidato
    }

    public static Candidato fromRequestDTOToEntity(CandidatoRequestDTO dto) {
        Candidato candidato = new Candidato()
        candidato.nome = dto.nome
        candidato.sobrenome = dto.sobrenome
        candidato.dataNascimento = dto.dataNascimento?.toString()
        candidato.email = dto.email
        candidato.cpf = dto.cpf
        candidato.pais = dto.pais
        candidato.estado = dto.estado
        candidato.descricao = dto.descricao
        candidato.cep = dto.cep
        candidato.senha = dto.senha
        return candidato
    }
}
