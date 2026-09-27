package org.ugustavodev.mscompetencia.model

import jakarta.persistence.*

@Entity
@Table(name = "competencias_candidatos")
class CompetenciaCandidato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column(name = "candidato_id", nullable = false)
    Long candidatoId

    @ManyToOne(optional = false)
    @JoinColumn(name = "competencia_id", nullable = false)
    Competencia competencia

    @Column(nullable = false)
    String nivel
}
