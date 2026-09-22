package org.ugustavodev.msnucleo.model

import jakarta.persistence.*
import org.ugustavodev.msnucleo.domain.Usuario

@MappedSuperclass
class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column(nullable = false)
    String nome

    @Column(nullable = false)
    String pais

    @Column
    String estado

    @Column
    String cep

    @Column(columnDefinition = "TEXT")
    String descricao

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    Usuario usuario
}
