package org.ugustavodev.msnucleo.dto.response

import java.time.LocalDate

record CandidatoResponseDTO(
        Long id,
        String nome,
        String sobrenome,
        LocalDate dataNascimento,
        String email,
        String cpf,
        String pais,
        String estado,
        String cep,
        String descricao
) {
}
