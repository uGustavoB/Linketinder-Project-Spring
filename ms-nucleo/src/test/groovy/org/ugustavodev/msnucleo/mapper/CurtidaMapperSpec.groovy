package org.ugustavodev.msnucleo.mapper

import org.ugustavodev.msnucleo.dto.response.CurtidaResponseDTO
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.model.Curtida
import org.ugustavodev.msnucleo.model.Vaga
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class CurtidaMapperSpec extends Specification {

    @Subject
    CurtidaMapper mapper = new CurtidaMapper()

    void "Deve converter entidade Curtida com Match para CurtidaResponseDTO"() {
        given: "Uma entidade Curtida com ambos os timestamps preenchidos"
        LocalDateTime dataCandidato = LocalDateTime.of(2026, 10, 23, 8, 30, 0)
        LocalDateTime dataEmpresa = LocalDateTime.of(2026, 10, 23, 9, 15, 0)
        Curtida curtida = new Curtida(
                id: 100L,
                candidato: new Candidato(id: 10L),
                vaga: new Vaga(id: 5L),
                candidatoCurtiuEm: dataCandidato,
                empresaCurtiuEm: dataEmpresa
        )

        when: "Converte para CurtidaResponseDTO"
        CurtidaResponseDTO dto = mapper.toResponseDTO(curtida)

        then: "Mapeia IDs, datas e identifica match como verdadeiro"
        dto.idCurtida == 100L
        dto.candidatoId == 10L
        dto.vagaId == 5L
        dto.candidatoCurtiuEm == dataCandidato
        dto.empresaCurtiuEm == dataEmpresa
        dto.match
    }

    void "Deve retornar nulo quando a entidade Curtida for nula"() {
        given: "Uma referencia nula de Curtida"
        Curtida curtida = null

        when: "Converte para CurtidaResponseDTO"
        CurtidaResponseDTO dto = mapper.toResponseDTO(curtida)

        then: "Retorna nulo"
        dto == null
    }

}
