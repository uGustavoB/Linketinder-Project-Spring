package org.ugustavodev.mscompetencia.usecase.impl

import org.ugustavodev.mscompetencia.dto.response.CompetenciaResponseDTO
import org.ugustavodev.mscompetencia.mapper.CompetenciaMapper
import org.ugustavodev.mscompetencia.model.Competencia
import org.ugustavodev.mscompetencia.repositories.CompetenciaRepository
import spock.lang.Specification
import spock.lang.Subject

class ListarCompetenciasUseCaseSpec extends Specification {

    CompetenciaRepository repository = Mock(CompetenciaRepository)
    CompetenciaMapper mapper = Mock(CompetenciaMapper)

    @Subject
    ListarCompetenciasUseCase useCase = new ListarCompetenciasUseCase(repository, mapper)

    void "Deve listar todas as competencias com sucesso"() {
        given: "Competencias cadastradas no repositorio"
        Competencia c1 = new Competencia(id: 1L, nome: "Java")
        Competencia c2 = new Competencia(id: 2L, nome: "Python")

        CompetenciaResponseDTO dto1 = new CompetenciaResponseDTO(1L, "Java")
        CompetenciaResponseDTO dto2 = new CompetenciaResponseDTO(2L, "Python")

        repository.findAll() >> [c1, c2]
        mapper.toResponseDTO(c1) >> dto1
        mapper.toResponseDTO(c2) >> dto2

        when: "Executa listagem"
        List<CompetenciaResponseDTO> result = useCase.executar()

        then: "Retorna a lista mapeada de competencias"
        result.size() == 2
        result[0].nome == "Java"
        result[1].nome == "Python"
    }
}
