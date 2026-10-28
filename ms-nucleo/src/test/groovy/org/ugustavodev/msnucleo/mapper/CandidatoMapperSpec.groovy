package org.ugustavodev.msnucleo.mapper

import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.dto.request.CandidatoRequestDTO
import org.ugustavodev.msnucleo.dto.response.CandidatoPublicoResponseDTO
import org.ugustavodev.msnucleo.dto.response.CandidatoResponseDTO
import org.ugustavodev.msnucleo.dto.response.FormacaoResponseDTO
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.model.Formacao
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

class CandidatoMapperSpec extends Specification {

    FormacaoMapper formacaoMapper = new FormacaoMapper()

    @Subject
    CandidatoMapper mapper = new CandidatoMapper(formacaoMapper)

    void "Deve converter entidade Candidato para CandidatoResponseDTO completo"() {
        given: "Uma entidade Candidato com formacao academica"
        LocalDate nascimento = LocalDate.of(1998, 5, 15)
        LocalDate inicioFormacao = LocalDate.of(2020, 2, 1)
        Formacao formacao = new Formacao(id: 1L, instituicao: "USP", curso: "Ciência da Computação", nivel: "Bacharelado", status: "Concluído", dataInicio: inicioFormacao)
        Candidato candidato = new Candidato(
                id: 10L,
                nome: "João",
                sobrenome: "Silva",
                dataNascimento: nascimento,
                cpf: "12345678901",
                pais: "Brasil",
                estado: "SP",
                cep: "01001000",
                descricao: "Dev Backend",
                usuario: new Usuario(email: "joao@email.com"),
                formacoes: [formacao]
        )

        when: "Converte para CandidatoResponseDTO"
        CandidatoResponseDTO dto = mapper.toResponseDTO(candidato)

        then: "Todos os atributos e formacoes sao mapeados corretamente"
        dto.id() == 10L
        dto.nome() == "João"
        dto.sobrenome() == "Silva"
        dto.dataNascimento() == nascimento
        dto.email() == "joao@email.com"
        dto.cpf() == "12345678901"
        dto.pais() == "Brasil"
        dto.estado() == "SP"
        dto.cep() == "01001000"
        dto.descricao() == "Dev Backend"
        dto.formacoes().size() == 1
        dto.formacoes()[0].instituicao() == "USP"
    }

    void "Deve converter entidade Candidato para CandidatoPublicoResponseDTO sem expor dados sensiveis"() {
        given: "Uma entidade Candidato completa"
        Candidato candidato = new Candidato(
                id: 10L,
                nome: "João",
                sobrenome: "Silva",
                cpf: "12345678901",
                pais: "Brasil",
                estado: "SP",
                descricao: "Dev Backend",
                usuario: new Usuario(email: "joao@email.com")
        )

        when: "Converte para CandidatoPublicoResponseDTO"
        CandidatoPublicoResponseDTO dto = mapper.toPublicoResponseDTO(candidato)

        then: "Apenas os dados publicos sao expostos"
        dto.id() == 10L
        dto.pais() == "Brasil"
        dto.estado() == "SP"
        dto.descricao() == "Dev Backend"
        dto.formacoes().isEmpty()
    }

    void "Deve converter CandidatoRequestDTO para entidade Candidato com papel CANDIDATO"() {
        given: "Um CandidatoRequestDTO valido"
        LocalDate nascimento = LocalDate.of(1998, 5, 15)
        CandidatoRequestDTO request = new CandidatoRequestDTO(
                nome: "João",
                sobrenome: "Silva",
                dataNascimento: nascimento,
                email: "JOAO@EMAIL.COM",
                senha: "senha123",
                cpf: "123.456.789-01",
                pais: "Brasil",
                estado: "SP",
                cep: "01001-000",
                descricao: "Dev Backend"
        )

        when: "Converte para entidade Candidato"
        Candidato entidade = mapper.fromRequestDTOToEntity(request)

        then: "Os dados sanitizados e o Usuario com Role.CANDIDATO sao preenchidos"
        entidade.nome == "João"
        entidade.sobrenome == "Silva"
        entidade.cpf == "12345678901"
        entidade.cep == "01001000"
        entidade.usuario.email == "joao@email.com"
        entidade.usuario.role == Role.CANDIDATO
    }
}
