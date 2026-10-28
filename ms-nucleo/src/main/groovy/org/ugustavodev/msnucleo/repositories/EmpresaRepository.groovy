package org.ugustavodev.msnucleo.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.ugustavodev.msnucleo.model.Empresa

interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    Optional<Empresa> findByUsuarioEmail(String email)
    Optional<Empresa> findByCnpj(String cnpj)

}
