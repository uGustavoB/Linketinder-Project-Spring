package org.ugustavodev.msnucleo.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "curtidas")
class Curtida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidato_id", nullable = false)
    Candidato candidato

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaga_id", nullable = false)
    Vaga vaga

    @Column(name = "candidato_curtiu_em")
    LocalDateTime candidatoCurtiuEm

    @Column(name = "empresa_curtiu_em")
    LocalDateTime empresaCurtiuEm

    boolean isMatch() {
        return candidatoCurtiuEm != null && empresaCurtiuEm != null
    }
}
