package org.ugustavodev.mscompetencia.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.ugustavodev.mscompetencia.model.CompetenciaVaga

interface CompetenciaVagaRepository extends JpaRepository<CompetenciaVaga, Long> {
    List<CompetenciaVaga> findByVagaId(Long vagaId)
    boolean existsByVagaIdAndCompetenciaId(Long vagaId, Long competenciaId)

    @Modifying
    @Query("DELETE FROM CompetenciaVaga cv WHERE cv.vagaId = :vagaId")
    void deleteByVagaId(@Param("vagaId") Long vagaId)

    @Modifying
    @Query("DELETE FROM CompetenciaVaga cv WHERE cv.empresaId = :empresaId")
    void deleteByEmpresaId(@Param("empresaId") Long empresaId)
}
