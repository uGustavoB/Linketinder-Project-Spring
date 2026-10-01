package org.ugustavodev.msnucleo.dto.request

import groovy.transform.Canonical
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Canonical
class VagaRequestDTO {
    @NotBlank(message = "O campo nome é obrigatório")
    String nome

    @NotBlank(message = "O campo descrição é obrigatório")
    String descricao

    String local
    String nivel

    @NotNull(message = "O campo empresaId é obrigatório")
    Long empresaId

    List<Long> competenciaIds = []

    void setNome(String nome) {
        this.nome = nome?.trim()
    }

    void setDescricao(String descricao) {
        this.descricao = descricao?.trim()
    }

    void setLocal(String local) {
        this.local = local?.trim()
    }

    void setNivel(String nivel) {
        this.nivel = nivel?.trim()
    }
}
