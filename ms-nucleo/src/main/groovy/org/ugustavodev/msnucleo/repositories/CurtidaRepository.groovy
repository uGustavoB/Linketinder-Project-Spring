package org.ugustavodev.msnucleo.repositories

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.ugustavodev.msnucleo.model.Curtida

interface CurtidaRepository extends JpaRepository<Curtida, Long> {

    Optional<Curtida> findByCandidatoIdAndVagaId(Long candidatoId, Long vagaId)

    @EntityGraph(attributePaths = ["vaga", "vaga.empresa", "vaga.empresa.usuario"])
    @Query("SELECT c FROM Curtida c WHERE c.candidato.id = :candidatoId AND c.candidatoCurtiuEm IS NOT NULL AND c.empresaCurtiuEm IS NOT NULL")
    List<Curtida> findMatchesByCandidatoId(@Param("candidatoId") Long candidatoId)

    @EntityGraph(attributePaths = ["vaga", "vaga.empresa", "candidato", "candidato.usuario", "candidato.formacoes"])
    @Query("SELECT c FROM Curtida c WHERE c.vaga.empresa.id = :empresaId AND c.candidatoCurtiuEm IS NOT NULL AND c.empresaCurtiuEm IS NOT NULL")
    List<Curtida> findMatchesByEmpresaId(@Param("empresaId") Long empresaId)

    @EntityGraph(attributePaths = ["vaga", "candidato", "candidato.usuario", "candidato.formacoes"])
    @Query("SELECT c FROM Curtida c WHERE c.vaga.id = :vagaId AND c.vaga.empresa.id = :empresaId AND c.candidatoCurtiuEm IS NOT NULL AND c.empresaCurtiuEm IS NOT NULL")
    List<Curtida> findMatchesByVagaIdAndEmpresaId(@Param("vagaId") Long vagaId, @Param("empresaId") Long empresaId)

    @Query("SELECT COUNT(c) > 0 FROM Curtida c WHERE c.candidato.id = :candidatoId AND c.vaga.id = :vagaId AND c.candidatoCurtiuEm IS NOT NULL AND c.empresaCurtiuEm IS NOT NULL")
    boolean existsMatchNaVaga(@Param("candidatoId") Long candidatoId, @Param("vagaId") Long vagaId)

    @Query("SELECT COUNT(c) > 0 FROM Curtida c WHERE c.candidato.id = :candidatoId AND c.vaga.empresa.id = :empresaId AND c.candidatoCurtiuEm IS NOT NULL AND c.empresaCurtiuEm IS NOT NULL")
    boolean existsMatchEntreCandidatoEEmpresa(@Param("candidatoId") Long candidatoId, @Param("empresaId") Long empresaId)

    @Modifying
    @Query("DELETE FROM Curtida c WHERE c.vaga.id = :vagaId")
    void deleteByVagaId(@Param("vagaId") Long vagaId)

    @Modifying
    @Query("DELETE FROM Curtida c WHERE c.candidato.id = :candidatoId")
    void deleteByCandidatoId(@Param("candidatoId") Long candidatoId)

    @Modifying
    @Query("DELETE FROM Curtida c WHERE c.vaga.id IN (SELECT v.id FROM Vaga v WHERE v.empresa.id = :empresaId)")
    void deleteByVagaEmpresaId(@Param("empresaId") Long empresaId)

}
