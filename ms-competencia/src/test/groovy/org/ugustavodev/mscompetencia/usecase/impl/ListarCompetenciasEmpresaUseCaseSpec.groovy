package org.ugustavodev.mscompetencia.usecase.impl

import org.ugustavodev.mscompetencia.dto.response.CompetenciaResponseDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVinculoResponseDTO
import org.ugustavodev.mscompetencia.mapper.CompetenciaVinculoMapper
import org.ugustavodev.mscompetencia.model.Competencia
import org.ugustavodev.mscompetencia.model.CompetenciaEmpresa
import org.ugustavodev.mscompetencia.model.enums.NivelCompetencia
import org.ugustavodev.mscompetencia.repositories.CompetenciaEmpresaRepository
import spock.lang.Specification
import spock.lang.Subject

class ListarCompetenciasEmpresaUseCaseSpec extends Specification {

    CompetenciaEmpresaRepository repository = Mock(CompetenciaEmpresaRepository)
    CompetenciaVinculoMapper mapper = Mock(CompetenciaVinculoMapper)

    @Subject
    ListarCompetenciasEmpresaUseCase useCase = new ListarCompetenciasEmpresaUseCase(repository, mapper)

    void "Deve lancar excecao quando empresaId for nulo"() {
        when: "Executa com empresaId nulo"
        useCase.executar(null)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID da empresa não pode ser nulo."
    }

    void "Deve retornar lista de competencias da empresa com sucesso"() {
        given: "Uma empresa com vinculos cadastrados"
        Long empresaId = 1L
        Competencia comp1 = new Competencia(id: 1L, nome: "Java")
        CompetenciaEmpresa vinculo = new CompetenciaEmpresa(id: 10L, empresaId: empresaId, competencia: comp1, nivel: NivelCompetencia.AVANCADO)
        CompetenciaVinculoResponseDTO dto = new CompetenciaVinculoResponseDTO(10L, new CompetenciaResponseDTO(1L, "Java"), NivelCompetencia.AVANCADO)

        repository.findByEmpresaId(empresaId) >> [vinculo]
        mapper.toResponseDTO(vinculo) >> dto

        when: "Executa a listagem"
        List<CompetenciaVinculoResponseDTO> result = useCase.executar(empresaId)

        then: "Retorna a lista mapeada"
        result.size() == 1
        result[0].idVinculo == 10L
        result[0].competencia.nome == "Java"
        result[0].nivel == NivelCompetencia.AVANCADO
    }
}
