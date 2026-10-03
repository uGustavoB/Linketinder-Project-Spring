package org.ugustavodev.mscompetencia.model

import jakarta.persistence.*

@Entity
@Table(name = "competencias_vagas")
class CompetenciaVaga {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column(name = "vaga_id", nullable = false)
    Long vagaId

    @ManyToOne(optional = false)
    @JoinColumn(name = "competencia_id", nullable = false)
    Competencia competencia
}
