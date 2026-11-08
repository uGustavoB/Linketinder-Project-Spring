package org.ugustavodev.mscompetencia.usecase.impl

import org.ugustavodev.mscompetencia.dto.request.CompetenciaRequestDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaResponseDTO
import org.ugustavodev.mscompetencia.mapper.CompetenciaMapper
import org.ugustavodev.mscompetencia.model.Competencia
import org.ugustavodev.mscompetencia.repositories.CompetenciaRepository
import spock.lang.Specification
import spock.lang.Subject

class CadastrarCompetenciaUseCaseSpec extends Specification {

    CompetenciaRepository repository = Mock(CompetenciaRepository)
    CompetenciaMapper mapper = Mock(CompetenciaMapper)

    @Subject
    CadastrarCompetenciaUseCase useCase = new CadastrarCompetenciaUseCase(repository, mapper)

    void "Deve lancar excecao quando requestDTO for nulo"() {
        when: "Executa com requestDTO nulo"
        useCase.executar(null)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O nome da competência é obrigatório."
        0 * repository.save(_)
    }

    void "Deve lancar excecao quando nome for nulo ou em branco"() {
        when: "Executa com nome em branco"
        useCase.executar(new CompetenciaRequestDTO(nome: "   "))

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O nome da competência é obrigatório."
        0 * repository.save(_)
    }

    void "Deve lancar excecao quando competencia com mesmo nome ja existir"() {
        given: "Nome ja existente no repositorio"
        String nome = "Kotlin"
        repository.existsByNomeIgnoreCase(nome) >> true

        when: "Tenta cadastrar competencia duplicada"
        useCase.executar(new CompetenciaRequestDTO(nome: nome))

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "Já existe uma competência com este nome."
        0 * repository.save(_)
    }

    void "Deve cadastrar nova competencia com sucesso"() {
        given: "Dados validos e nome inédito"
        String nome = "Rust"
        CompetenciaRequestDTO requestDTO = new CompetenciaRequestDTO(nome: nome)
        Competencia entidade = new Competencia(nome: nome)
        Competencia entidadeSalva = new Competencia(id: 10L, nome: nome)
        CompetenciaResponseDTO responseDTO = new CompetenciaResponseDTO(10L, nome)

        repository.existsByNomeIgnoreCase(nome) >> false
        mapper.toEntity(requestDTO) >> entidade
        repository.save(entidade) >> entidadeSalva
        mapper.toResponseDTO(entidadeSalva) >> responseDTO

        when: "Executa cadastro"
        CompetenciaResponseDTO result = useCase.executar(requestDTO)

        then: "Salva e retorna DTO com ID gerado"
        result.id == 10L
        result.nome == "Rust"
    }
}
