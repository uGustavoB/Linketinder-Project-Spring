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

    void atualizarDados(String nome, String descricao, String local, String nivel, List<Long> competenciaIds, Empresa empresa) {
        this.nome = nome
        this.descricao = descricao
        this.local = local
        this.nivel = nivel
        if (empresa != null) {
            this.empresa = empresa
        }
        if (competenciaIds != null) {
            this.competenciaIds = new ArrayList<>(competenciaIds)
        }
    }
}
