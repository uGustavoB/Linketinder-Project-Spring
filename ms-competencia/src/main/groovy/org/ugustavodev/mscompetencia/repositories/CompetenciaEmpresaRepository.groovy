package org.ugustavodev.mscompetencia.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.ugustavodev.mscompetencia.model.CompetenciaEmpresa

interface CompetenciaEmpresaRepository extends JpaRepository<CompetenciaEmpresa, Long> {
    List<CompetenciaEmpresa> findByEmpresaId(Long empresaId)
    Optional<CompetenciaEmpresa> findByEmpresaIdAndCompetenciaId(Long empresaId, Long competenciaId)
    boolean existsByEmpresaIdAndCompetenciaId(Long empresaId, Long competenciaId)
}
