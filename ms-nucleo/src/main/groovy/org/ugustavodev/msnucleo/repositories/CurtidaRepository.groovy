package org.ugustavodev.msnucleo.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.ugustavodev.msnucleo.model.Curtida
import java.util.Optional

interface CurtidaRepository extends JpaRepository<Curtida, Long> {

    Optional<Curtida> findByCandidatoIdAndVagaId(Long candidatoId, Long vagaId)

    @Query("SELECT c FROM Curtida c WHERE c.candidato.id = :candidatoId AND c.candidatoCurtiuEm IS NOT NULL AND c.empresaCurtiuEm IS NOT NULL")
    List<Curtida> findMatchesByCandidatoId(@Param("candidatoId") Long candidatoId)

    @Query("SELECT c FROM Curtida c WHERE c.vaga.empresa.id = :empresaId AND c.candidatoCurtiuEm IS NOT NULL AND c.empresaCurtiuEm IS NOT NULL")
    List<Curtida> findMatchesByEmpresaId(@Param("empresaId") Long empresaId)

    @Query("SELECT c FROM Curtida c WHERE c.vaga.id = :vagaId AND c.vaga.empresa.id = :empresaId AND c.candidatoCurtiuEm IS NOT NULL AND c.empresaCurtiuEm IS NOT NULL")
    List<Curtida> findMatchesByVagaIdAndEmpresaId(@Param("vagaId") Long vagaId, @Param("empresaId") Long empresaId)

    @Query("SELECT COUNT(c) > 0 FROM Curtida c WHERE c.candidato.id = :candidatoId AND c.vaga.id = :vagaId AND c.candidatoCurtiuEm IS NOT NULL AND c.empresaCurtiuEm IS NOT NULL")
    boolean existsMatchNaVaga(@Param("candidatoId") Long candidatoId, @Param("vagaId") Long vagaId)

    @Query("SELECT COUNT(c) > 0 FROM Curtida c WHERE c.candidato.id = :candidatoId AND c.vaga.empresa.id = :empresaId AND c.candidatoCurtiuEm IS NOT NULL AND c.empresaCurtiuEm IS NOT NULL")
    boolean existsMatchEntreCandidatoEEmpresa(@Param("candidatoId") Long candidatoId, @Param("empresaId") Long empresaId)

    void deleteByVagaId(Long vagaId)
    void deleteByCandidatoId(Long candidatoId)
    void deleteByVagaEmpresaId(Long empresaId)

}
