package org.ugustavodev.msnucleo.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "empresas")
class Empresa extends Pessoa {
    @Column(nullable = false, unique = true, length = 14)
    String cnpj

    void atualizarDados(String nome, String cnpj, String email, String pais, String estado, String cep, String descricao, String senhaCriptografada) {
        this.nome = nome
        this.cnpj = cnpj
        this.usuario.email = email
        this.pais = pais
        this.estado = estado
        this.cep = cep
        this.descricao = descricao
        if (senhaCriptografada != null && !senhaCriptografada.trim().isEmpty()) {
            this.usuario.senha = senhaCriptografada
        }
    }
}
