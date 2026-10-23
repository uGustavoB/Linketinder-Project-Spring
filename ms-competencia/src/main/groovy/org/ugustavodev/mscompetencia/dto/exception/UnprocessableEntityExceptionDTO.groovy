package org.ugustavodev.mscompetencia.dto.exception

record UnprocessableEntityExceptionDTO(
        String campo,
        String mensagem
) {}
