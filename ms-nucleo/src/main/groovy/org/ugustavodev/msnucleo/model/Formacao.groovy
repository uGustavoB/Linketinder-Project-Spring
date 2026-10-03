package org.ugustavodev.msnucleo.model

import jakarta.persistence.*
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
