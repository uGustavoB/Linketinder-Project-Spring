package org.ugustavodev.msnucleo.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "candidatos")
class Candidato extends Pessoa {
    @Column(nullable = false)
    String sobrenome

    @Column(name = "data_nascimento", nullable = false)
    String dataNascimento

    @Column(nullable = false, unique = true, length = 11)
    String cpf
}
