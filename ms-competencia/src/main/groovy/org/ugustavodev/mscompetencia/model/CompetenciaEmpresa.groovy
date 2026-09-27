package org.ugustavodev.mscompetencia.model

import jakarta.persistence.*
import org.ugustavodev.mscompetencia.model.enums.NivelCompetencia

@Entity
@Table(name = "competencias_empresas")
class CompetenciaEmpresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column(name = "empresa_id", nullable = false)
    Long empresaId

    @ManyToOne(optional = false)
    @JoinColumn(name = "competencia_id", nullable = false)
    Competencia competencia

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    NivelCompetencia nivel
}
