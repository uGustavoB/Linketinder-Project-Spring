package org.ugustavodev.mscompetencia.usecase.impl

import org.ugustavodev.mscompetencia.dto.request.VincularCompetenciaRequestDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaResponseDTO
import org.ugustavodev.mscompetencia.dto.response.CompetenciaVinculoResponseDTO
import org.ugustavodev.mscompetencia.mapper.CompetenciaVinculoMapper
import org.ugustavodev.mscompetencia.model.Competencia
import org.ugustavodev.mscompetencia.model.CompetenciaCandidato
import org.ugustavodev.mscompetencia.model.enums.NivelCompetencia
import org.ugustavodev.mscompetencia.repositories.CompetenciaCandidatoRepository
import org.ugustavodev.mscompetencia.repositories.CompetenciaRepository
import spock.lang.Specification
import spock.lang.Subject

class VincularCompetenciaCandidatoUseCaseSpec extends Specification {

    CompetenciaCandidatoRepository competenciaCandidatoRepository = Mock(CompetenciaCandidatoRepository)
    CompetenciaRepository competenciaRepository = Mock(CompetenciaRepository)
    CompetenciaVinculoMapper competenciaVinculoMapper = Mock(CompetenciaVinculoMapper)

    @Subject
    VincularCompetenciaCandidatoUseCase useCase = new VincularCompetenciaCandidatoUseCase(
            competenciaCandidatoRepository,
            competenciaRepository,
            competenciaVinculoMapper
    )

    void "Deve lancar excecao quando candidatoId for nulo"() {
        when: "Executa com candidatoId nulo"
        useCase.executar(null, [new VincularCompetenciaRequestDTO(competenciaId: 1L, nivel: NivelCompetencia.AVANCADO)])

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID do candidato não pode ser nulo."
    }

    void "Deve retornar lista vazia quando requestDTOs for nulo"() {
        when: "Executa com lista nula"
        List<CompetenciaVinculoResponseDTO> result = useCase.executar(1L, null)

        then: "Retorna lista vazia"
        result.isEmpty()
    }

    void "Deve lancar excecao quando item de request for nulo"() {
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

    void "Deve lancar NoSuchElementException quando competencia generica nao for encontrada"() {
        given: "Candidato sem vinculo e competencia inexistente"
        Long candidatoId = 1L
        Long compId = 99L

        competenciaCandidatoRepository.findByCandidatoIdAndCompetenciaId(candidatoId, compId) >> Optional.empty()
        competenciaRepository.findById(compId) >> Optional.empty()

        when: "Tenta vincular competencia inexistente"
        useCase.executar(candidatoId, [new VincularCompetenciaRequestDTO(competenciaId: compId, nivel: NivelCompetencia.BASICO)])

        then: "Lanca NoSuchElementException"
        NoSuchElementException ex = thrown(NoSuchElementException)
        ex.message == "Competência com ID ${compId} não encontrada."
    }

    void "Deve atualizar nivel de competencia existente (upsert) com sucesso"() {
        given: "Um vinculo existente com nivel BASICO"
        Long candidatoId = 1L
        Long compId = 5L
        Competencia comp = new Competencia(id: compId, nome: "Java")
        CompetenciaCandidato vinculoExistente = new CompetenciaCandidato(id: 10L, candidatoId: candidatoId, competencia: comp, nivel: NivelCompetencia.BASICO)

        competenciaCandidatoRepository.findByCandidatoIdAndCompetenciaId(candidatoId, compId) >> Optional.of(vinculoExistente)

        CompetenciaCandidato vinculoAtualizado = new CompetenciaCandidato(id: 10L, candidatoId: candidatoId, competencia: comp, nivel: NivelCompetencia.AVANCADO)
        CompetenciaVinculoResponseDTO responseDTO = new CompetenciaVinculoResponseDTO(10L, new CompetenciaResponseDTO(compId, "Java"), NivelCompetencia.AVANCADO)
        competenciaVinculoMapper.toResponseDTO(vinculoAtualizado) >> responseDTO

        when: "Atualiza para AVANCADO"
        List<CompetenciaVinculoResponseDTO> result = useCase.executar(candidatoId, [new VincularCompetenciaRequestDTO(competenciaId: compId, nivel: NivelCompetencia.AVANCADO)])

        then: "Nao busca no competenciaRepository e atualiza o vinculo existente"
        0 * competenciaRepository.findById(_)
        1 * competenciaCandidatoRepository.save({ CompetenciaCandidato c -> c.id == 10L && c.nivel == NivelCompetencia.AVANCADO }) >> vinculoAtualizado
        result.size() == 1
        result[0].nivel == NivelCompetencia.AVANCADO
    }

    void "Deve cadastrar novo vinculo de competencia (insert) com sucesso"() {
        given: "Candidato sem o vinculo e competencia cadastrada"
        Long candidatoId = 1L
        Long compId = 5L
        Competencia comp = new Competencia(id: compId, nome: "Docker")

        competenciaCandidatoRepository.findByCandidatoIdAndCompetenciaId(candidatoId, compId) >> Optional.empty()
        competenciaRepository.findById(compId) >> Optional.of(comp)

        CompetenciaCandidato vinculoSalvo = new CompetenciaCandidato(id: 15L, candidatoId: candidatoId, competencia: comp, nivel: NivelCompetencia.MEDIO)
        competenciaCandidatoRepository.save({ CompetenciaCandidato c -> c.candidatoId == candidatoId && c.competencia == comp && c.nivel == NivelCompetencia.MEDIO }) >> vinculoSalvo

        CompetenciaVinculoResponseDTO responseDTO = new CompetenciaVinculoResponseDTO(15L, new CompetenciaResponseDTO(compId, "Docker"), NivelCompetencia.MEDIO)
        competenciaVinculoMapper.toResponseDTO(vinculoSalvo) >> responseDTO

        when: "Executa vinculacao nova"
        List<CompetenciaVinculoResponseDTO> result = useCase.executar(candidatoId, [new VincularCompetenciaRequestDTO(competenciaId: compId, nivel: NivelCompetencia.MEDIO)])

        then: "Salva novo vinculo e retorna DTO correspondente"
        result.size() == 1
        result[0].idVinculo == 15L
        result[0].competencia.nome == "Docker"
        result[0].nivel == NivelCompetencia.MEDIO
    }
}
