package org.ugustavodev.msnucleo.repositories

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.ugustavodev.msnucleo.model.Vaga

interface VagaRepository extends JpaRepository<Vaga, Long> {

    @EntityGraph(attributePaths = ["empresa", "empresa.usuario"])
    List<Vaga> findByEmpresaId(Long empresaId)

    long countByEmpresaId(Long empresaId)

    @EntityGraph(attributePaths = ["empresa", "empresa.usuario"])
    @Query("SELECT v FROM Vaga v WHERE v.id NOT IN (SELECT c.vaga.id FROM Curtida c WHERE c.candidato.id = :candidatoId AND c.candidatoCurtiuEm IS NOT NULL)")
    List<Vaga> findVagasSugeridasParaCandidato(@Param("candidatoId") Long candidatoId)

    @Query("SELECT COUNT(v) FROM Vaga v WHERE v.id NOT IN (SELECT c.vaga.id FROM Curtida c WHERE c.candidato.id = :candidatoId AND c.candidatoCurtiuEm IS NOT NULL)")
    long countVagasSugeridasParaCandidato(@Param("candidatoId") Long candidatoId)

    @Modifying
    @Query("DELETE FROM Vaga v WHERE v.empresa.id = :empresaId")
    void deleteByEmpresaId(@Param("empresaId") Long empresaId)

}
