package org.ugustavodev.mscompetencia.usecase.impl

import org.ugustavodev.mscompetencia.dto.request.VincularCompetenciaRequestDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaResponseDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVinculoResponseDTO
import org.ugustavodev.mscompetencia.mapper.CompetenciaVinculoMapper
import org.ugustavodev.mscompetencia.model.Competencia
import org.ugustavodev.mscompetencia.model.CompetenciaEmpresa
import org.ugustavodev.mscompetencia.model.enums.NivelCompetencia
import org.ugustavodev.mscompetencia.repositories.CompetenciaEmpresaRepository
import org.ugustavodev.mscompetencia.repositories.CompetenciaRepository
import spock.lang.Specification
import spock.lang.Subject

class VincularCompetenciaEmpresaUseCaseSpec extends Specification {

    CompetenciaEmpresaRepository repository = Mock(CompetenciaEmpresaRepository)
    CompetenciaRepository competenciaRepository = Mock(CompetenciaRepository)
    CompetenciaVinculoMapper mapper = Mock(CompetenciaVinculoMapper)

    @Subject
    VincularCompetenciaEmpresaUseCase useCase = new VincularCompetenciaEmpresaUseCase(repository, competenciaRepository, mapper)

    void "Deve lancar excecao quando empresaId for nulo"() {
        when: "Executa com empresaId nulo"
        useCase.executar(null, [new VincularCompetenciaRequestDTO(competenciaId: 1L, nivel: NivelCompetencia.AVANCADO)])

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID da empresa não pode ser nulo."
    }

    void "Deve retornar lista vazia quando requestDTOs for nulo"() {
        when: "Executa com requestDTOs nulo"
        List<CompetenciaVinculoResponseDTO> result = useCase.executar(1L, null)

        then: "Retorna lista vazia"
        result.isEmpty()
    }

    void "Deve lancar excecao quando item da lista for nulo"() {
        when: "Executa com item nulo na lista"
        useCase.executar(1L, [null])

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "Os dados de vinculação da competência não podem ser nulos."
    }

    void "Deve lancar excecao quando competenciaId for nulo"() {
        when: "Executa com competenciaId nulo"
        useCase.executar(1L, [new VincularCompetenciaRequestDTO(competenciaId: null, nivel: NivelCompetencia.BASICO)])

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID da competência é obrigatório."
    }

    void "Deve lancar excecao quando nivel for nulo"() {
        when: "Executa com nivel nulo"
        useCase.executar(1L, [new VincularCompetenciaRequestDTO(competenciaId: 1L, nivel: null)])

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O nível da competência é obrigatório."
    }

    void "Deve pular competencia quando a empresa ja possuir o vinculo"() {
        given: "Empresa ja possui o vinculo"
        Long empresaId = 1L
        Long compId = 5L

        repository.existsByEmpresaIdAndCompetenciaId(empresaId, compId) >> true

        when: "Tenta vincular competencia repetida"
        List<CompetenciaVinculoResponseDTO> result = useCase.executar(empresaId, [new VincularCompetenciaRequestDTO(competenciaId: compId, nivel: NivelCompetencia.BASICO)])

        then: "Nao busca no repositorio de competencias e nao salva nada"
        0 * competenciaRepository.findById(_)
        0 * repository.save(_)
        result.isEmpty()
    }

    void "Deve lancar NoSuchElementException quando competencia generica nao for encontrada"() {
        given: "Competencia inexistente no catalogo"
        Long empresaId = 1L
        Long compId = 99L

        repository.existsByEmpresaIdAndCompetenciaId(empresaId, compId) >> false
        competenciaRepository.findById(compId) >> Optional.empty()

        when: "Tenta vincular competencia inexistente"
        useCase.executar(empresaId, [new VincularCompetenciaRequestDTO(competenciaId: compId, nivel: NivelCompetencia.MEDIO)])

        then: "Lanca NoSuchElementException"
        NoSuchElementException ex = thrown(NoSuchElementException)
        ex.message == "Competência genérica com ID ${compId} não encontrada."
        0 * repository.save(_)
    }

    void "Deve vincular competencias a empresa com sucesso"() {
        given: "Empresa e competencias validas"
        Long empresaId = 1L
        Long compId = 2L
        Competencia comp = new Competencia(id: compId, nome: "Angular")

        repository.existsByEmpresaIdAndCompetenciaId(empresaId, compId) >> false
        competenciaRepository.findById(compId) >> Optional.of(comp)

        CompetenciaEmpresa vinculoSalvo = new CompetenciaEmpresa(id: 10L, empresaId: empresaId, competencia: comp, nivel: NivelCompetencia.AVANCADO)
        repository.save({ CompetenciaEmpresa e -> e.empresaId == empresaId && e.competencia == comp && e.nivel == NivelCompetencia.AVANCADO }) >> vinculoSalvo

        CompetenciaVinculoResponseDTO responseDTO = new CompetenciaVinculoResponseDTO(10L, new CompetenciaResponseDTO(compId, "Angular"), NivelCompetencia.AVANCADO)
        mapper.toResponseDTO(vinculoSalvo) >> responseDTO

        when: "Executa vinculacao"
        List<CompetenciaVinculoResponseDTO> result = useCase.executar(empresaId, [new VincularCompetenciaRequestDTO(competenciaId: compId, nivel: NivelCompetencia.AVANCADO)])

        then: "Salva e retorna DTO mapeado"
        result.size() == 1
        result[0].idVinculo == 10L
        result[0].competencia.nome == "Angular"
        result[0].nivel == NivelCompetencia.AVANCADO
    }
}
