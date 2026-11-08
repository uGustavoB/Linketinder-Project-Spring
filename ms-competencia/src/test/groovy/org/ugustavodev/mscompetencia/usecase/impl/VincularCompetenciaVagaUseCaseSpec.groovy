package org.ugustavodev.mscompetencia.usecase.impl

import org.springframework.security.access.AccessDeniedException
import org.ugustavodev.mscompetencia.dto.request.VincularCompetenciaVagaRequestDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaResponseDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVagaVinculoResponseDTO
import org.ugustavodev.mscompetencia.mapper.CompetenciaVinculoMapper
import org.ugustavodev.mscompetencia.model.Competencia
import org.ugustavodev.mscompetencia.model.CompetenciaVaga
import org.ugustavodev.mscompetencia.repositories.CompetenciaRepository
import org.ugustavodev.mscompetencia.repositories.CompetenciaVagaRepository
import spock.lang.Specification
import spock.lang.Subject

class VincularCompetenciaVagaUseCaseSpec extends Specification {

    CompetenciaVagaRepository repository = Mock(CompetenciaVagaRepository)
    CompetenciaRepository competenciaRepository = Mock(CompetenciaRepository)
    CompetenciaVinculoMapper mapper = Mock(CompetenciaVinculoMapper)

    @Subject
    VincularCompetenciaVagaUseCase useCase = new VincularCompetenciaVagaUseCase(repository, competenciaRepository, mapper)

    void "Deve lancar excecao quando vagaId for nulo"() {
        when: "Executa com vagaId nulo"
        useCase.executar(null, 1L, [new VincularCompetenciaVagaRequestDTO(competenciaId: 1L)])

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID da vaga não pode ser nulo."
    }

    void "Deve lancar AccessDeniedException quando empresaId for nulo"() {
        when: "Executa com empresaId nulo"
        useCase.executar(10L, null, [new VincularCompetenciaVagaRequestDTO(competenciaId: 1L)])

        then: "Lanca AccessDeniedException"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Você não tem permissão para vincular competências a esta vaga."
    }

    void "Deve retornar lista vazia quando a lista de DTOs for nula"() {
        when: "Executa com requestDTOs nulo"
        List<CompetenciaVagaVinculoResponseDTO> result = useCase.executar(10L, 1L, null)

        then: "Retorna lista vazia"
        result.isEmpty()
    }

    void "Deve lancar AccessDeniedException quando vaga pertencer a outra empresa (protecao IDOR)"() {
        given: "Um vinculo existente na vaga pertencente a outra empresa"
        Long vagaId = 10L
        Long empresaAutenticadaId = 1L
        Long outraEmpresaId = 2L

        CompetenciaVaga vinculoExistente = new CompetenciaVaga(id: 100L, vagaId: vagaId, empresaId: outraEmpresaId)
        repository.findByVagaId(vagaId) >> [vinculoExistente]

        when: "A empresa tenta vincular competencias na vaga de outra empresa"
        useCase.executar(vagaId, empresaAutenticadaId, [new VincularCompetenciaVagaRequestDTO(competenciaId: 1L)])

        then: "Lanca AccessDeniedException"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Você não tem permissão para alterar as competências de uma vaga de outra empresa."
        0 * repository.save(_)
    }

    void "Deve lancar excecao quando item da lista de request for nulo"() {
        given: "Vaga sem vinculos previos"
        repository.findByVagaId(10L) >> []

        when: "Executa com item nulo na lista"
        useCase.executar(10L, 1L, [null])

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "Os dados de vinculação da competência não podem ser nulos."
    }

    void "Deve lancar excecao quando competenciaId for nulo"() {
        given: "Vaga sem vinculos previos"
        repository.findByVagaId(10L) >> []

        when: "Executa com competenciaId nulo"
        useCase.executar(10L, 1L, [new VincularCompetenciaVagaRequestDTO(competenciaId: null)])

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID da competência é obrigatório."
    }

    void "Deve pular competencia quando a vaga ja possuir o vinculo"() {
        given: "Vaga com vinculo previo pertencente a mesma empresa"
        Long vagaId = 10L
        Long empresaId = 1L
        Long competenciaId = 5L

        CompetenciaVaga vinculoExistente = new CompetenciaVaga(id: 100L, vagaId: vagaId, empresaId: empresaId)
        repository.findByVagaId(vagaId) >> [vinculoExistente]
        repository.existsByVagaIdAndCompetenciaId(vagaId, competenciaId) >> true

        when: "Tenta vincular competencia repetida"
        List<CompetenciaVagaVinculoResponseDTO> result = useCase.executar(vagaId, empresaId, [new VincularCompetenciaVagaRequestDTO(competenciaId: competenciaId)])

        then: "Nao busca no competenciaRepository e nao salva nada"
        0 * competenciaRepository.findById(_)
        0 * repository.save(_)
        result.isEmpty()
    }

    void "Deve lancar NoSuchElementException quando competencia informada nao for encontrada"() {
        given: "Vaga sem vinculos previos e competencia inexistente"
        Long vagaId = 10L
        Long empresaId = 1L
        Long competenciaId = 99L

        repository.findByVagaId(vagaId) >> []
        repository.existsByVagaIdAndCompetenciaId(vagaId, competenciaId) >> false
        competenciaRepository.findById(competenciaId) >> Optional.empty()

        when: "Tenta vincular competencia inexistente"
        useCase.executar(vagaId, empresaId, [new VincularCompetenciaVagaRequestDTO(competenciaId: competenciaId)])

        then: "Lanca NoSuchElementException"
        NoSuchElementException ex = thrown(NoSuchElementException)
        ex.message == "Competência genérica com ID ${competenciaId} não encontrada."
        0 * repository.save(_)
    }

    void "Deve vincular competencias a vaga com sucesso salvando empresaId"() {
        given: "Uma vaga valida e competencias existentes"
        Long vagaId = 10L
        Long empresaId = 1L
        Long compId1 = 1L
        Long compId2 = 2L

        Competencia comp1 = new Competencia(id: compId1, nome: "Java")
        Competencia comp2 = new Competencia(id: compId2, nome: "Spring Boot")

        repository.findByVagaId(vagaId) >> []
        repository.existsByVagaIdAndCompetenciaId(vagaId, compId1) >> false
        repository.existsByVagaIdAndCompetenciaId(vagaId, compId2) >> false

        competenciaRepository.findById(compId1) >> Optional.of(comp1)
        competenciaRepository.findById(compId2) >> Optional.of(comp2)

        CompetenciaVaga vinculoSalvo1 = new CompetenciaVaga(id: 101L, vagaId: vagaId, empresaId: empresaId, competencia: comp1)
        CompetenciaVaga vinculoSalvo2 = new CompetenciaVaga(id: 102L, vagaId: vagaId, empresaId: empresaId, competencia: comp2)

        CompetenciaVagaVinculoResponseDTO dto1 = new CompetenciaVagaVinculoResponseDTO(101L, new CompetenciaResponseDTO(compId1, "Java"))
        CompetenciaVagaVinculoResponseDTO dto2 = new CompetenciaVagaVinculoResponseDTO(102L, new CompetenciaResponseDTO(compId2, "Spring Boot"))

        mapper.toVagaResponseDTO(vinculoSalvo1) >> dto1
        mapper.toVagaResponseDTO(vinculoSalvo2) >> dto2

        when: "Executa a vinculacao"
        List<CompetenciaVagaVinculoResponseDTO> result = useCase.executar(
                vagaId,
                empresaId,
                [new VincularCompetenciaVagaRequestDTO(competenciaId: compId1), new VincularCompetenciaVagaRequestDTO(competenciaId: compId2)]
        )

        then: "Salva os 2 vinculos com os dados corretos"
        1 * repository.save({ CompetenciaVaga v -> v.vagaId == vagaId && v.empresaId == empresaId && v.competencia == comp1 }) >> vinculoSalvo1
        1 * repository.save({ CompetenciaVaga v -> v.vagaId == vagaId && v.empresaId == empresaId && v.competencia == comp2 }) >> vinculoSalvo2

        and: "Retorna a lista mapeada"
        result.size() == 2
        result[0].idVinculo == 101L
        result[0].competencia.nome == "Java"
        result[1].idVinculo == 102L
        result[1].competencia.nome == "Spring Boot"
    }
}
