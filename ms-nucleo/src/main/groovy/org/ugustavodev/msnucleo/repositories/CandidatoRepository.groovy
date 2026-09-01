package org.ugustavodev.msnucleo.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.ugustavodev.msnucleo.model.Candidato

interface CandidatoRepository extends JpaRepository<Candidato, Long> {
    Optional<Candidato> findByEmail(String email)
    Optional<Candidato> findByCpf(String cpf)
}
