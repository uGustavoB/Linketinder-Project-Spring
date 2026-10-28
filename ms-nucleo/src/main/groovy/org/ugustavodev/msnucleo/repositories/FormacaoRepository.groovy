package org.ugustavodev.msnucleo.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.ugustavodev.msnucleo.model.Formacao

interface FormacaoRepository extends JpaRepository<Formacao, Long> {

}
