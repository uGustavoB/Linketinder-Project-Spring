package org.ugustavodev.mscompetencia.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.ugustavodev.mscompetencia.model.CompetenciaEmpresa

interface CompetenciaEmpresaRepository extends JpaRepository<CompetenciaEmpresa, Long> {
    List<CompetenciaEmpresa> findByEmpresaId(Long empresaId)
    Optional<CompetenciaEmpresa> findByEmpresaIdAndCompetenciaId(Long empresaId, Long competenciaId)
    boolean existsByEmpresaIdAndCompetenciaId(Long empresaId, Long competenciaId)

    @Modifying
    @Query("DELETE FROM CompetenciaEmpresa ce WHERE ce.empresaId = :empresaId")
    void deleteByEmpresaId(@Param("empresaId") Long empresaId)
}
