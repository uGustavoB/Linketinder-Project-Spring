package org.ugustavodev.msnucleo.model

import jakarta.persistence.*

@Entity
@Table(name = "vagas")
class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column(nullable = false)
    String nome

    @Column(columnDefinition = "TEXT", nullable = false)
    String descricao

    @Column
    String local

    @Column
    String nivel

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "empresa_id", nullable = false)
    Empresa empresa

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "vaga_competencias", joinColumns = @JoinColumn(name = "vaga_id"))
    @Column(name = "competencia_id")
    List<Long> competenciaIds = []
}
