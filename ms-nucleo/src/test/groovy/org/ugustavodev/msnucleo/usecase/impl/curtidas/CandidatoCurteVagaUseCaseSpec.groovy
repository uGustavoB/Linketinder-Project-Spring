package org.ugustavodev.msnucleo.usecase.impl.curtidas

import org.ugustavodev.msnucleo.dto.response.CurtidaResponseDTO
import org.ugustavodev.msnucleo.mapper.CurtidaMapper
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.model.Curtida
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.repositories.CurtidaRepository
import org.ugustavodev.msnucleo.repositories.VagaRepository
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class CandidatoCurteVagaUseCaseSpec extends Specification {

    CurtidaRepository curtidaRepository = Mock(CurtidaRepository)
    CandidatoRepository candidatoRepository = Mock(CandidatoRepository)
    VagaRepository vagaRepository = Mock(VagaRepository)
    CurtidaMapper curtidaMapper = Mock(CurtidaMapper)

    @Subject
    CandidatoCurteVagaUseCase useCase = new CandidatoCurteVagaUseCase(
            curtidaRepository,
            candidatoRepository,
            vagaRepository,
            curtidaMapper
    )

    void "Deve lancar excecao quando idCandidato for nulo"() {
        given: "Um idCandidato nulo e idVaga valido"
        Long idCandidato = null
        Long idVaga = 5L

        when: "Executa a curtida"
        useCase.executar(idCandidato, idVaga)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID do candidato não pode ser nulo."
    }

    void "Deve lancar excecao quando idVaga for nulo"() {
        given: "Um idCandidato valido e idVaga nulo"
        Long idCandidato = 1L
        Long idVaga = null

        when: "Executa a curtida"
        useCase.executar(idCandidato, idVaga)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O ID da vaga não pode ser nulo."
    }

    void "Deve criar nova curtida unilateral quando nao existir registro previo"() {
        given: "Um candidato e uma vaga existentes sem curtida previa"
        Long idCandidato = 1L
        Long idVaga = 5L
        Candidato candidato = new Candidato(id: idCandidato)
        Vaga vaga = new Vaga(id: idVaga)
        Curtida curtidaSalva = new Curtida(id: 10L, candidato: candidato, vaga: vaga, candidatoCurtiuEm: LocalDateTime.now())
        CurtidaResponseDTO esperado = new CurtidaResponseDTO(idCurtida: 10L, candidatoId: idCandidato, vagaId: idVaga, match: false)

        1 * candidatoRepository.findById(idCandidato) >> Optional.of(candidato)
        1 * vagaRepository.findById(idVaga) >> Optional.of(vaga)
        1 * curtidaRepository.findByCandidatoIdAndVagaId(idCandidato, idVaga) >> Optional.empty()
        1 * curtidaRepository.save(_ as Curtida) >> curtidaSalva
        1 * curtidaMapper.toResponseDTO(curtidaSalva) >> esperado

        when: "O candidato curte a vaga"
        CurtidaResponseDTO resultado = useCase.executar(idCandidato, idVaga)

        then: "Retorna a curtida registrada com match falso"
        resultado == esperado
        !resultado.match
    }

    void "Deve consolidar Match quando a empresa ja houver curtido o candidato na vaga"() {
        given: "Uma curtida previa da empresa para o candidato na vaga"
        Long idCandidato = 1L
        Long idVaga = 5L
        Candidato candidato = new Candidato(id: idCandidato)
        Vaga vaga = new Vaga(id: idVaga)
        Curtida curtidaPrevia = new Curtida(
                id: 10L,
                candidato: candidato,
                vaga: vaga,
                empresaCurtiuEm: LocalDateTime.of(2026, 10, 23, 8, 0, 0)
        )
        CurtidaResponseDTO esperado = new CurtidaResponseDTO(idCurtida: 10L, candidatoId: idCandidato, vagaId: idVaga, match: true)

        1 * candidatoRepository.findById(idCandidato) >> Optional.of(candidato)
        1 * vagaRepository.findById(idVaga) >> Optional.of(vaga)
        1 * curtidaRepository.findByCandidatoIdAndVagaId(idCandidato, idVaga) >> Optional.of(curtidaPrevia)
        1 * curtidaRepository.save(curtidaPrevia) >> curtidaPrevia
        1 * curtidaMapper.toResponseDTO(curtidaPrevia) >> esperado

        when: "O candidato curte a vaga"
        CurtidaResponseDTO resultado = useCase.executar(idCandidato, idVaga)

        then: "Preenche candidatoCurtiuEm e consolida o Match"
        curtidaPrevia.candidatoCurtiuEm != null
        curtidaPrevia.isMatch()
        resultado.match
    }

    void "Deve ser idempotente quando o candidato ja houver curtido a vaga anteriormente"() {
        given: "Uma curtida onde candidatoCurtiuEm ja esta preenchido"
        Long idCandidato = 1L
        Long idVaga = 5L
        LocalDateTime dataOriginal = LocalDateTime.of(2026, 10, 20, 10, 0, 0)
        Candidato candidato = new Candidato(id: idCandidato)
        Vaga vaga = new Vaga(id: idVaga)
        Curtida curtidaExistente = new Curtida(id: 10L, candidato: candidato, vaga: vaga, candidatoCurtiuEm: dataOriginal)
        CurtidaResponseDTO esperado = new CurtidaResponseDTO(idCurtida: 10L, candidatoId: idCandidato, vagaId: idVaga, candidatoCurtiuEm: dataOriginal, match: false)

        1 * candidatoRepository.findById(idCandidato) >> Optional.of(candidato)
        1 * vagaRepository.findById(idVaga) >> Optional.of(vaga)
        1 * curtidaRepository.findByCandidatoIdAndVagaId(idCandidato, idVaga) >> Optional.of(curtidaExistente)
        0 * curtidaRepository.save(_)
        1 * curtidaMapper.toResponseDTO(curtidaExistente) >> esperado

        when: "O candidato curte novamente a mesma vaga"
        CurtidaResponseDTO resultado = useCase.executar(idCandidato, idVaga)

        then: "Preserva a data original sem salvar novamente"
        curtidaExistente.candidatoCurtiuEm == dataOriginal
        resultado == esperado
    }

}
