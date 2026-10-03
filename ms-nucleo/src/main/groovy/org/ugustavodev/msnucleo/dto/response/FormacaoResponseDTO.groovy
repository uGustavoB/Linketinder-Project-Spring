package org.ugustavodev.msnucleo.dto.response

import java.time.LocalDate

record FormacaoResponseDTO(
        Long id,
        String instituicao,
        String curso,
        String nivel,
        String status,
        LocalDate dataInicio,
        LocalDate dataConclusao
) {}
