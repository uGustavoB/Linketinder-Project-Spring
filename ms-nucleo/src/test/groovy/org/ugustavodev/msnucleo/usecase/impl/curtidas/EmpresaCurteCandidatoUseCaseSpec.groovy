package org.ugustavodev.msnucleo.usecase.impl.curtidas

import org.springframework.security.access.AccessDeniedException
import org.ugustavodev.msnucleo.dto.response.CurtidaResponseDTO
import org.ugustavodev.msnucleo.mapper.CurtidaMapper
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.model.Curtida
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.model.Vaga
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.repositories.CurtidaRepository
import org.ugustavodev.msnucleo.repositories.VagaRepository
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class EmpresaCurteCandidatoUseCaseSpec extends Specification {

    CurtidaRepository curtidaRepository = Mock(CurtidaRepository)
    CandidatoRepository candidatoRepository = Mock(CandidatoRepository)
    VagaRepository vagaRepository = Mock(VagaRepository)
    CurtidaMapper curtidaMapper = Mock(CurtidaMapper)

    @Subject
    EmpresaCurteCandidatoUseCase useCase = new EmpresaCurteCandidatoUseCase(
            curtidaRepository,
            candidatoRepository,
            vagaRepository,
            curtidaMapper
    )

    void "Deve lancar AccessDeniedException quando a vaga nao pertencer a empresa autenticada"() {
        given: "Uma vaga pertencente a empresa 1 e tentativa de curtida pela empresa 2"
        Long idEmpresaLogada = 2L
        Long idCandidato = 10L
        Long idVaga = 5L
        Candidato candidato = new Candidato(id: idCandidato)
        Vaga vaga = new Vaga(id: idVaga, empresa: new Empresa(id: 1L))

        1 * candidatoRepository.findById(idCandidato) >> Optional.of(candidato)
        1 * vagaRepository.findById(idVaga) >> Optional.of(vaga)

        when: "A empresa 2 tenta curtir candidato na vaga da empresa 1"
        useCase.executar(idEmpresaLogada, idCandidato, idVaga)

        then: "Lanca AccessDeniedException"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "Esta vaga não pertence à sua empresa."
        0 * curtidaRepository.save(_)
    }

    void "Deve consolidar Match quando o candidato ja houver curtido a vaga previamente"() {
        given: "Uma curtida previa do candidato na vaga da empresa"
        Long idEmpresa = 1L
        Long idCandidato = 10L
        Long idVaga = 5L
        Candidato candidato = new Candidato(id: idCandidato)
        Vaga vaga = new Vaga(id: idVaga, empresa: new Empresa(id: idEmpresa))
        Curtida curtidaPrevia = new Curtida(
                id: 100L,
                candidato: candidato,
                vaga: vaga,
                candidatoCurtiuEm: LocalDateTime.of(2026, 10, 23, 8, 30, 0)
        )
        CurtidaResponseDTO esperado = new CurtidaResponseDTO(idCurtida: 100L, candidatoId: idCandidato, vagaId: idVaga, match: true)

        1 * candidatoRepository.findById(idCandidato) >> Optional.of(candidato)
        1 * vagaRepository.findById(idVaga) >> Optional.of(vaga)
        1 * curtidaRepository.findByCandidatoIdAndVagaId(idCandidato, idVaga) >> Optional.of(curtidaPrevia)
        1 * curtidaRepository.save(curtidaPrevia) >> curtidaPrevia
        1 * curtidaMapper.toResponseDTO(curtidaPrevia) >> esperado

        when: "A empresa curte o candidato na vaga"
        CurtidaResponseDTO resultado = useCase.executar(idEmpresa, idCandidato, idVaga)

        then: "Preenche empresaCurtiuEm e consolida o Match"
        curtidaPrevia.empresaCurtiuEm != null
        curtidaPrevia.isMatch()
        resultado.match
    }

}
