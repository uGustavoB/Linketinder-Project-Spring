package org.ugustavodev.msnucleo.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.OneToOne
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
