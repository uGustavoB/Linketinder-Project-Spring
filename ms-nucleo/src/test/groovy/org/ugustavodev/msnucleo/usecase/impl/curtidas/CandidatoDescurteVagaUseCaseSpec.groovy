package org.ugustavodev.msnucleo.usecase.impl.curtidas

import org.ugustavodev.msnucleo.model.Curtida
import org.ugustavodev.msnucleo.repositories.CurtidaRepository
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class CandidatoDescurteVagaUseCaseSpec extends Specification {

    CurtidaRepository curtidaRepository = Mock(CurtidaRepository)

    @Subject
    CandidatoDescurteVagaUseCase useCase = new CandidatoDescurteVagaUseCase(curtidaRepository)

    void "Deve lancar excecao quando o candidato nao tiver curtido a vaga"() {
        given: "Um registro de curtida onde apenas a empresa curtiu"
        Long idCandidato = 1L
        Long idVaga = 5L
        Curtida curtida = new Curtida(id: 10L, candidatoCurtiuEm: null, empresaCurtiuEm: LocalDateTime.now())
        1 * curtidaRepository.findByCandidatoIdAndVagaId(idCandidato, idVaga) >> Optional.of(curtida)

        when: "O candidato tenta descurtir"
        useCase.executar(idCandidato, idVaga)

        then: "Lanca IllegalStateException"
        IllegalStateException ex = thrown(IllegalStateException)
        ex.message == "O candidato não curtiu esta vaga."
    }

    void "Deve deletar fisicamente o registro quando a empresa nao houver curtido"() {
        given: "Uma curtida unilateral apenas do candidato"
        Long idCandidato = 1L
        Long idVaga = 5L
        Curtida curtida = new Curtida(id: 10L, candidatoCurtiuEm: LocalDateTime.now(), empresaCurtiuEm: null)
        1 * curtidaRepository.findByCandidatoIdAndVagaId(idCandidato, idVaga) >> Optional.of(curtida)

        when: "O candidato descurte a vaga"
        useCase.executar(idCandidato, idVaga)

        then: "Deleta o registro de curtida do banco"
        1 * curtidaRepository.delete(curtida)
        0 * curtidaRepository.save(_)
    }

    void "Deve manter o registro limpando apenas candidatoCurtiuEm quando a empresa tambem houver curtido"() {
        given: "Um registro com Match (ambos curtiram)"
        Long idCandidato = 1L
        Long idVaga = 5L
        LocalDateTime dataEmpresa = LocalDateTime.of(2026, 10, 23, 8, 0, 0)
        Curtida curtida = new Curtida(id: 10L, candidatoCurtiuEm: LocalDateTime.now(), empresaCurtiuEm: dataEmpresa)
        1 * curtidaRepository.findByCandidatoIdAndVagaId(idCandidato, idVaga) >> Optional.of(curtida)

        when: "O candidato descurte a vaga"
        useCase.executar(idCandidato, idVaga)

        then: "Zera candidatoCurtiuEm preservando empresaCurtiuEm e salva o registro"
        curtida.candidatoCurtiuEm == null
        curtida.empresaCurtiuEm == dataEmpresa
        1 * curtidaRepository.save(curtida)
        0 * curtidaRepository.delete(_)
    }

}
