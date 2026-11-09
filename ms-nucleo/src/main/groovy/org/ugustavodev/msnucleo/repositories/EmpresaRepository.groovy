package org.ugustavodev.msnucleo.repositories

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.ugustavodev.msnucleo.model.Empresa

interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    @EntityGraph(attributePaths = ["usuario"])
    Optional<Empresa> findByUsuarioEmail(String email)

    @EntityGraph(attributePaths = ["usuario"])
    Optional<Empresa> findByCnpj(String cnpj)

    @EntityGraph(attributePaths = ["usuario"])
    Optional<Empresa> findById(Long id)

    @EntityGraph(attributePaths = ["usuario"])
    List<Empresa> findAll()

}
