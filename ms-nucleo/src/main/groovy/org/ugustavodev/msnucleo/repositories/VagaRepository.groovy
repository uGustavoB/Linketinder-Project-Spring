package org.ugustavodev.msnucleo.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.ugustavodev.msnucleo.model.Vaga

interface VagaRepository extends JpaRepository<Vaga, Long> {
    List<Vaga> findByEmpresaId(Long empresaId)
    
    @Query("SELECT v FROM Vaga v WHERE v.id NOT IN (SELECT c.vaga.id FROM Curtida c WHERE c.candidato.id = :candidatoId AND c.candidatoCurtiuEm IS NOT NULL)")
    List<Vaga> findVagasSugeridasParaCandidato(@Param("candidatoId") Long candidatoId)
}
