package org.ugustavodev.msnucleo.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "empresas")
class Empresa extends Pessoa {
    @Column(nullable = false, unique = true, length = 14)
    String cnpj
}
