package org.ugustavodev.msnucleo.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "candidatos")
class Candidato extends Pessoa {
    @Column(nullable = false)
    String sobrenome

    @Column(name = "data_nascimento", nullable = false)
    String dataNascimento

    @Column(nullable = false, unique = true, length = 11)
    String cpf

    void atualizarDados(String nome, String sobrenome, String dataNascimento, String email, String cpf, String pais, String estado, String cep, String descricao, String senhaCriptografada) {
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
