package org.ugustavodev.msnucleo.dto.exception

record UnprocessableEntityExceptionDTO(
        String campo,
        String mensagem
) {
}
