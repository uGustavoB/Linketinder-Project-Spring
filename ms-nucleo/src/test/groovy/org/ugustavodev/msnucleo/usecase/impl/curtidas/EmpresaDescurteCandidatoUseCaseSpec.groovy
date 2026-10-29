package org.ugustavodev.msnucleo.usecase.impl.curtidas

import org.springframework.security.access.AccessDeniedException
import org.ugustavodev.msnucleo.model.Curtida
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.CurtidaRepository
import org.ugustavodev.msnucleo.repositories.VagaRepository
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class EmpresaDescurteCandidatoUseCaseSpec extends Specification {

    CurtidaRepository curtidaRepository = Mock(CurtidaRepository)
    VagaRepository vagaRepository = Mock(VagaRepository)

    @Subject
    EmpresaDescurteCandidatoUseCase useCase = new EmpresaDescurteCandidatoUseCase(curtidaRepository, vagaRepository)

    void "Deve lancar AccessDeniedException quando a vaga pertencer a outra empresa"() {
        given: "Uma vaga da empresa 1 e tentativa de descurtida pela empresa 2"
        Long idEmpresa = 2L
        Long idCandidato = 10L
        Long idVaga = 5L
        Vaga vaga = new Vaga(id: idVaga, empresa: new Empresa(id: 1L))
        1 * vagaRepository.findById(idVaga) >> Optional.of(vaga)

        when: "Executa a descurtida"
        useCase.executar(idEmpresa, idCandidato, idVaga)

        then: "Lanca AccessDeniedException"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Esta vaga não pertence à sua empresa."
    }

    void "Deve manter o registro limpando apenas empresaCurtiuEm quando o candidato tambem houver curtido"() {
        given: "Uma vaga da empresa e um registro com Match"
        Long idEmpresa = 1L
        Long idCandidato = 10L
        Long idVaga = 5L
        LocalDateTime dataCandidato = LocalDateTime.of(2026, 10, 23, 8, 30, 0)
        Vaga vaga = new Vaga(id: idVaga, empresa: new Empresa(id: idEmpresa))
        Curtida curtida = new Curtida(id: 100L, candidatoCurtiuEm: dataCandidato, empresaCurtiuEm: LocalDateTime.now())

        1 * vagaRepository.findById(idVaga) >> Optional.of(vaga)
        1 * curtidaRepository.findByCandidatoIdAndVagaId(idCandidato, idVaga) >> Optional.of(curtida)

        when: "A empresa descurte o candidato"
        useCase.executar(idEmpresa, idCandidato, idVaga)

        then: "Zera empresaCurtiuEm preservando candidatoCurtiuEm e salva o registro"
        curtida.empresaCurtiuEm == null
        curtida.candidatoCurtiuEm == dataCandidato
        1 * curtidaRepository.save(curtida)
        0 * curtidaRepository.delete(_)
    }

    void "Deve deletar fisicamente o registro quando o candidato nao houver curtido"() {
        given: "Uma curtida unilateral apenas da empresa"
        Long idEmpresa = 1L
        Long idCandidato = 10L
        Long idVaga = 5L
        Vaga vaga = new Vaga(id: idVaga, empresa: new Empresa(id: idEmpresa))
        Curtida curtida = new Curtida(id: 100L, candidatoCurtiuEm: null, empresaCurtiuEm: LocalDateTime.now())

        1 * vagaRepository.findById(idVaga) >> Optional.of(vaga)
        1 * curtidaRepository.findByCandidatoIdAndVagaId(idCandidato, idVaga) >> Optional.of(curtida)

        when: "A empresa descurte o candidato"
        useCase.executar(idEmpresa, idCandidato, idVaga)

        then: "Deleta fisicamente o registro"
        1 * curtidaRepository.delete(curtida)
        0 * curtidaRepository.save(_)
    }

}
