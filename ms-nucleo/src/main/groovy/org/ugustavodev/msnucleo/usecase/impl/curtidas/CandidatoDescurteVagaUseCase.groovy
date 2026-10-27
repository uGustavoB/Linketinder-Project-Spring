package org.ugustavodev.msnucleo.usecase.impl.curtidas

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.model.Curtida
import org.ugustavodev.msnucleo.repositories.CurtidaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.curtidas.ICandidatoDescurteVagaUseCase

@Service
class CandidatoDescurteVagaUseCase implements ICandidatoDescurteVagaUseCase {
    private final CurtidaRepository curtidaRepository

    CandidatoDescurteVagaUseCase(CurtidaRepository curtidaRepository) {
        this.curtidaRepository = curtidaRepository
    }

    @Override
    void executar(Long idCandidato, Long idVaga) {
        if (idCandidato == null) {
            throw new IllegalArgumentException("O ID do candidato não pode ser nulo.")
        }
        if (idVaga == null) {
            throw new IllegalArgumentException("O ID da vaga não pode ser nulo.")
        }

        Curtida curtida = curtidaRepository.findByCandidatoIdAndVagaId(idCandidato, idVaga)
                .orElseThrow(() -> new NoSuchElementException("Curtida não encontrada."))

        if (curtida.getCandidatoCurtiuEm() == null) {
            throw new IllegalStateException("O candidato não curtiu esta vaga.")
        }

        if (curtida.getEmpresaCurtiuEm() != null) {
            curtida.setCandidatoCurtiuEm(null)
            curtidaRepository.save(curtida)
        } else {
            curtidaRepository.delete(curtida)
        }
    }
}
