package org.ugustavodev.msnucleo.mapper

import org.ugustavodev.msnucleo.dto.request.FormacaoRequestDTO
import org.ugustavodev.msnucleo.dto.response.FormacaoResponseDTO
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.model.Formacao
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

class FormacaoMapperSpec extends Specification {

    @Subject
    FormacaoMapper mapper = new FormacaoMapper()

    void "Deve converter entidade Formacao para FormacaoResponseDTO"() {
        given: "Uma entidade Formacao preenchida"
        LocalDate inicio = LocalDate.of(2020, 2, 1)
        LocalDate conclusao = LocalDate.of(2023, 12, 15)
        Formacao formacao = new Formacao(
                id: 1L,
                instituicao: "USP",
                curso: "Ciência da Computação",
                nivel: "Bacharelado",
                status: "Concluído",
                dataInicio: inicio,
                dataConclusao: conclusao
        )

        when: "Converte para FormacaoResponseDTO"
        FormacaoResponseDTO dto = mapper.toResponseDTO(formacao)

        then: "Todos os atributos sao mapeados corretamente"
        dto.id() == 1L
        dto.instituicao() == "USP"
        dto.curso() == "Ciência da Computação"
        dto.nivel() == "Bacharelado"
        dto.status() == "Concluído"
        dto.dataInicio() == inicio
        dto.dataConclusao() == conclusao
    }

    void "Deve converter FormacaoRequestDTO e Candidato para entidade Formacao"() {
        given: "Um FormacaoRequestDTO e um Candidato"
        LocalDate inicio = LocalDate.of(2020, 2, 1)
        LocalDate conclusao = LocalDate.of(2023, 12, 15)
        FormacaoRequestDTO request = new FormacaoRequestDTO(
                instituicao: "USP",
                curso: "Ciência da Computação",
                nivel: "Bacharelado",
                status: "Concluído",
                dataInicio: inicio,
                dataConclusao: conclusao
        )
        Candidato candidato = new Candidato(id: 10L)

        when: "Converte para entidade Formacao"
        Formacao formacao = mapper.fromRequestDTOToEntity(request, candidato)

        then: "Preenche todos os campos e associa o candidato"
        formacao.instituicao == "USP"
        formacao.curso == "Ciência da Computação"
        formacao.nivel == "Bacharelado"
        formacao.status == "Concluído"
        formacao.dataInicio == inicio
        formacao.dataConclusao == conclusao
        formacao.candidato == candidato
    }
}
