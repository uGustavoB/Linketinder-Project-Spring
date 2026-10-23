package org.ugustavodev.mscompetencia.model

import jakarta.persistence.*

@Entity
@Table(
        name = "competencias_vagas",
        uniqueConstraints = [
                @UniqueConstraint(name = "uk_competencia_vaga", columnNames = ["vaga_id", "competencia_id"])
        ]
)
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
