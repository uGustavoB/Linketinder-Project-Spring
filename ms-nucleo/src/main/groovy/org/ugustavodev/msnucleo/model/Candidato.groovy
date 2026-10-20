package org.ugustavodev.msnucleo.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

import jakarta.persistence.OneToMany
import jakarta.persistence.CascadeType
import jakarta.persistence.FetchType
import org.ugustavodev.msnucleo.dto.request.CandidatoRequestDTO

import java.time.LocalDate

@Entity
@Table(name = "candidatos")
class Candidato extends Pessoa {
    @Column(nullable = false)
    String sobrenome

    @Column(name = "data_nascimento", nullable = false)
    LocalDate dataNascimento

    @Column(nullable = false, unique = true, length = 11)
    String cpf

    @OneToMany(mappedBy = "candidato", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Formacao> formacoes = new ArrayList<>()

    void atualizarDados(String nome, String sobrenome, LocalDate dataNascimento, String email, String cpf, String pais, String estado, String cep, String descricao, String senhaCriptografada) {
        this.nome = nome
        this.sobrenome = sobrenome
        this.dataNascimento = dataNascimento
        this.usuario.email = email
        this.cpf = cpf
        this.pais = pais
        this.estado = estado
        this.cep = cep
        this.descricao = descricao
        if (senhaCriptografada != null && !senhaCriptografada.trim().isEmpty()) {
            this.usuario.senha = senhaCriptografada
        }
    }
}
