package org.ugustavodev.msnucleo.dto.exception

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Detalhe de erro de validação de um campo específico (HTTP 422)")
record UnprocessableEntityExceptionDTO(
        @Schema(description = "Nome do campo que falhou na validação", example = "email")
        String campo,

        @Schema(description = "Mensagem explicativa da regra violada", example = "O campo email deve ser um email válido")
        String mensagem
) {
}
