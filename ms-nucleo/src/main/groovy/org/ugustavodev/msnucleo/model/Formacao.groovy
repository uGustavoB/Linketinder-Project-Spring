package org.ugustavodev.msnucleo.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "formacoes")
class Formacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column(nullable = false)
    String instituicao

    @Column(nullable = false)
    String curso

    @Column(nullable = false)
    String nivel

    @Column(nullable = false)
    String status

    @Column(name = "data_inicio")
    LocalDate dataInicio

    @Column(name = "data_conclusao")
    LocalDate dataConclusao

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidato_id", nullable = false)
    Candidato candidato

}
