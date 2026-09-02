package org.ugustavodev.msnucleo.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.ugustavodev.msnucleo.model.Vaga

interface VagaRepository extends JpaRepository<Vaga, Long> {
    List<Vaga> findByEmpresaId(Long empresaId)
}
