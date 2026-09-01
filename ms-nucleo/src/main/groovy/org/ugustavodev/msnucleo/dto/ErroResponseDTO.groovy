package org.ugustavodev.msnucleo.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class ErroResponseDTO {
    int codigo
    String mensagem
    String tipo = "Erro"
    List<Object> erros
}
