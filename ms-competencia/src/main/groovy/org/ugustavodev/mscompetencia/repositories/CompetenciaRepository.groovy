package org.ugustavodev.mscompetencia.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.ugustavodev.mscompetencia.model.Competencia

interface CompetenciaRepository extends JpaRepository<Competencia, Long> {
    boolean existsByNomeIgnoreCase(String nome)
}
