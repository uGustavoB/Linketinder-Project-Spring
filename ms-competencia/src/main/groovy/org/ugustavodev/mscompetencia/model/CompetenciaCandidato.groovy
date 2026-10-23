package org.ugustavodev.mscompetencia.model

import jakarta.persistence.*
import org.ugustavodev.mscompetencia.model.enums.NivelCompetencia

@Entity
@Table(
        name = "competencias_candidatos",
        uniqueConstraints = [
                @UniqueConstraint(name = "uk_competencia_candidato", columnNames = ["candidato_id", "competencia_id"])
        ]
)
class CompetenciaCandidato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column(name = "candidato_id", nullable = false)
    Long candidatoId

    @ManyToOne(optional = false)
    @JoinColumn(name = "competencia_id", nullable = false)
    Competencia competencia

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    NivelCompetencia nivel
}
