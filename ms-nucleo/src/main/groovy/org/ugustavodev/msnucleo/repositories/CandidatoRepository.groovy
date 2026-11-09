package org.ugustavodev.msnucleo.repositories

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.ugustavodev.msnucleo.model.Candidato

interface CandidatoRepository extends JpaRepository<Candidato, Long> {

    @EntityGraph(attributePaths = ["formacoes", "usuario"])
    Optional<Candidato> findByUsuarioEmail(String email)

    @EntityGraph(attributePaths = ["formacoes", "usuario"])
    Optional<Candidato> findByCpf(String cpf)

    @EntityGraph(attributePaths = ["formacoes", "usuario"])
    Optional<Candidato> findById(Long id)

    @EntityGraph(attributePaths = ["formacoes", "usuario"])
    List<Candidato> findAll()

    @EntityGraph(attributePaths = ["formacoes", "usuario"])
    @Query("SELECT DISTINCT c FROM Candidato c WHERE c.id NOT IN (SELECT cu.candidato.id FROM Curtida cu WHERE cu.vaga.id = :vagaId AND cu.empresaCurtiuEm IS NOT NULL)")
    List<Candidato> findCandidatosSugeridosParaVaga(@Param("vagaId") Long vagaId)

    @EntityGraph(attributePaths = ["formacoes", "usuario"])
    @Query("SELECT DISTINCT c FROM Candidato c JOIN Curtida cu ON c.id = cu.candidato.id WHERE cu.vaga.id = :vagaId AND cu.candidatoCurtiuEm IS NOT NULL AND cu.empresaCurtiuEm IS NULL")
    List<Candidato> findCandidatosInteressadosNaVaga(@Param("vagaId") Long vagaId)

}
