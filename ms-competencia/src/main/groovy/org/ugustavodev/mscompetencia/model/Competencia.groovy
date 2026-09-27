package org.ugustavodev.mscompetencia.model

import jakarta.persistence.*

@Entity
@Table(name = "competencias")
class Competencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column(nullable = false, unique = true)
    String nome
}
