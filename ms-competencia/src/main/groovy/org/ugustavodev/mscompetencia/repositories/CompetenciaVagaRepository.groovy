package org.ugustavodev.mscompetencia.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.ugustavodev.mscompetencia.model.CompetenciaVaga

interface CompetenciaVagaRepository extends JpaRepository<CompetenciaVaga, Long> {
    List<CompetenciaVaga> findByVagaId(Long vagaId)
    boolean existsByVagaIdAndCompetenciaId(Long vagaId, Long competenciaId)
}
