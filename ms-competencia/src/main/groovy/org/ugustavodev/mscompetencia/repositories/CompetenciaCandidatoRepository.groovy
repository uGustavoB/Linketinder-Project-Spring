package org.ugustavodev.mscompetencia.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.ugustavodev.mscompetencia.dto.response.CandidatosPorCompetenciaResponseDTO
import org.ugustavodev.mscompetencia.model.CompetenciaCandidato

interface CompetenciaCandidatoRepository extends JpaRepository<CompetenciaCandidato, Long> {
    List<CompetenciaCandidato> findByCandidatoId(Long candidatoId)
    boolean existsByCandidatoIdAndCompetenciaId(Long candidatoId, Long competenciaId)
    Optional<CompetenciaCandidato> findByCandidatoIdAndCompetenciaId(Long candidatoId, Long competenciaId)

    @Query("""
        SELECT new org.ugustavodev.mscompetencia.dto.response.CandidatosPorCompetenciaResponseDTO(
            c.id,
            c.nome,
            COUNT(DISTINCT cc.candidatoId)
        )
        FROM Competencia c
        LEFT JOIN CompetenciaCandidato cc ON cc.competencia.id = c.id
        GROUP BY c.id, c.nome
        ORDER BY COUNT(DISTINCT cc.candidatoId) DESC, c.nome ASC
    """)
    List<CandidatosPorCompetenciaResponseDTO> contarCandidatosPorCompetencia()
}
