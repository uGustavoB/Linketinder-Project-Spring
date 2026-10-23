package org.ugustavodev.mscompetencia.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.ugustavodev.mscompetencia.model.CompetenciaCandidato

interface CompetenciaCandidatoRepository extends JpaRepository<CompetenciaCandidato, Long> {
    List<CompetenciaCandidato> findByCandidatoId(Long candidatoId)
    boolean existsByCandidatoIdAndCompetenciaId(Long candidatoId, Long competenciaId)
    Optional<CompetenciaCandidato> findByCandidatoIdAndCompetenciaId(Long candidatoId, Long competenciaId)
}
