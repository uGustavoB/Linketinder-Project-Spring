package org.ugustavodev.msnucleo.dto.response

import java.time.LocalDate

record PerfilCandidatoDTO(
        Long id,
        String tipoUsuario,
        String nome,
        String sobrenome,
        LocalDate dataNascimento,
        String email,
        String cpf,
        String pais,
        String estado,
        String cep,
        String descricao
) {}
