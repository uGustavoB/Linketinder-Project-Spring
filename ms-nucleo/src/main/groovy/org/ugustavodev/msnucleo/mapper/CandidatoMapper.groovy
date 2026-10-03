package org.ugustavodev.msnucleo.mapper

import org.springframework.stereotype.Component
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.dto.request.CandidatoRequestDTO
import org.ugustavodev.msnucleo.dto.response.CandidatoResponseDTO
import org.ugustavodev.msnucleo.dto.response.CandidatoPublicoResponseDTO
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.dto.response.FormacaoResponseDTO

import java.time.LocalDate

@Component
class CandidatoMapper {

    private final FormacaoMapper formacaoMapper

    CandidatoMapper(FormacaoMapper formacaoMapper) {
        this.formacaoMapper = formacaoMapper
    }

    CandidatoResponseDTO toResponseDTO(Candidato candidato) {
        List<FormacaoResponseDTO> formacoesDTO = candidato.formacoes?.stream()
                ?.map(formacaoMapper::toResponseDTO)
                ?.toList() ?: new ArrayList<>()

        return new CandidatoResponseDTO(
                candidato.id,
                candidato.nome,
                candidato.sobrenome,
                LocalDate.parse(candidato.dataNascimento),
                candidato.usuario?.email,
                candidato.cpf,
                candidato.pais,
                candidato.estado,
                candidato.cep,
                candidato.descricao,
                formacoesDTO
        )
    }

    CandidatoPublicoResponseDTO toPublicoResponseDTO(Candidato candidato) {
        List<FormacaoResponseDTO> formacoesDTO = candidato.formacoes?.stream()
                ?.map(formacaoMapper::toResponseDTO)
                ?.toList() ?: new ArrayList<>()

        return new CandidatoPublicoResponseDTO(
                candidato.id,
                candidato.pais,
                candidato.estado,
                candidato.descricao,
                formacoesDTO
        )
    }

    Candidato fromResponseDTOToEntity(CandidatoResponseDTO dto) {
        Candidato candidato = new Candidato()
        candidato.id = dto.id()
        candidato.nome = dto.nome()
        candidato.sobrenome = dto.sobrenome()
        candidato.dataNascimento = dto.dataNascimento().toString()
        candidato.usuario = new Usuario(email: dto.email())
        candidato.cpf = dto.cpf()
        candidato.pais = dto.pais()
        candidato.estado = dto.estado()
        candidato.cep = dto.cep()
        candidato.descricao = dto.descricao()
        // formacoes should be updated via specific endpoints
        return candidato
    }

    Candidato fromRequestDTOToEntity(CandidatoRequestDTO dto) {
        Candidato candidato = new Candidato()
        candidato.nome = dto.nome
        candidato.sobrenome = dto.sobrenome
        candidato.dataNascimento = dto.dataNascimento?.toString()
        candidato.usuario = new Usuario(email: dto.email, senha: dto.senha, role: Role.CANDIDATO)
        candidato.cpf = dto.cpf
        candidato.pais = dto.pais
        candidato.estado = dto.estado
        candidato.descricao = dto.descricao
        candidato.cep = dto.cep
        return candidato
    }
}
