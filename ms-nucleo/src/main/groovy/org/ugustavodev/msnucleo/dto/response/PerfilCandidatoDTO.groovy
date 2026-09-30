package org.ugustavodev.msnucleo.dto.response

record PerfilCandidatoDTO(
        String tipoUsuario,
        String nome,
        String sobrenome,
        String dataNascimento,
        String email,
        String cpf,
        String pais,
        String estado,
        String cep,
        String descricao
) {}
