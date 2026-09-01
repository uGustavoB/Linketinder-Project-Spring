package org.ugustavodev.msnucleo.model

import jakarta.persistence.*

@MappedSuperclass
class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column(nullable = false)
    String nome

    @Column(nullable = false, unique = true)
    String email

    @Column(nullable = false)
    String pais

    @Column
    String estado

    @Column
    String cep

    @Column(columnDefinition = "TEXT")
    String descricao

    @Column(nullable = false)
    String senha
}
