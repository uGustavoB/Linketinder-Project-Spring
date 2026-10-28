package org.ugustavodev.msnucleo.mapper

import org.ugustavodev.msnucleo.dto.request.VagaRequestDTO
import org.ugustavodev.msnucleo.dto.response.VagaPublicoResponseDTO
import org.ugustavodev.msnucleo.dto.response.VagaResponseDTO
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.model.Vaga
import spock.lang.Specification
import spock.lang.Subject

class VagaMapperSpec extends Specification {

    @Subject
    VagaMapper mapper = new VagaMapper()

    void "Deve converter entidade Vaga para VagaResponseDTO com empresaId e empresaNome"() {
        given: "Uma entidade Vaga vinculada a uma Empresa"
        Empresa empresa = new Empresa(id: 1L, nome: "Tech Solutions Ltda")
        Vaga vaga = new Vaga(id: 5L, nome: "Dev Groovy", descricao: "Spring Boot", local: "Remoto", nivel: "Pleno", empresa: empresa)

        when: "Converte para VagaResponseDTO"
        VagaResponseDTO dto = mapper.toResponseDTO(vaga)

        then: "Mapeia todos os atributos incluindo dados da empresa"
        dto.id() == 5L
        dto.nome() == "Dev Groovy"
        dto.descricao() == "Spring Boot"
        dto.local() == "Remoto"
        dto.nivel() == "Pleno"
        dto.empresaId() == 1L
        dto.empresaNome() == "Tech Solutions Ltda"
    }

    void "Deve converter entidade Vaga para VagaPublicoResponseDTO sem empresaNome"() {
        given: "Uma entidade Vaga vinculada a uma Empresa"
        Empresa empresa = new Empresa(id: 1L, nome: "Tech Solutions Ltda")
        Vaga vaga = new Vaga(id: 5L, nome: "Dev Groovy", descricao: "Spring Boot", local: "Remoto", nivel: "Pleno", empresa: empresa)

        when: "Converte para VagaPublicoResponseDTO"
        VagaPublicoResponseDTO dto = mapper.toPublicoResponseDTO(vaga)

        then: "Mapeia os atributos publicos da vaga"
        dto.id() == 5L
        dto.nome() == "Dev Groovy"
        dto.descricao() == "Spring Boot"
        dto.local() == "Remoto"
        dto.nivel() == "Pleno"
        dto.empresaId() == 1L
    }

    void "Deve converter VagaRequestDTO e Empresa para entidade Vaga"() {
        given: "Um VagaRequestDTO e uma Empresa"
        VagaRequestDTO request = new VagaRequestDTO(nome: "Dev Groovy", descricao: "Spring Boot", local: "Remoto", nivel: "Pleno")
        Empresa empresa = new Empresa(id: 1L, nome: "Tech Solutions Ltda")

        when: "Converte para entidade Vaga"
        Vaga vaga = mapper.fromRequestDTOToEntity(request, empresa)

        then: "Preenche todos os campos e associa a empresa"
        vaga.nome == "Dev Groovy"
        vaga.descricao == "Spring Boot"
        vaga.local == "Remoto"
        vaga.nivel == "Pleno"
        vaga.empresa == empresa
    }
}
