package org.ugustavodev.msnucleo.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class ApiResponseDTO<T> {
    int codigo
    String mensagem
    String tipo
    T dados
    List<Object> erros

    static <T> ApiResponseDTO<T> sucesso(T dados, String mensagem = "Operação realizada com sucesso", int codigo = 200) {
        return new ApiResponseDTO<T>(
                codigo: codigo,
                mensagem: mensagem,
                tipo: "Sucesso",
                dados: dados
        )
    }

    static <T> ApiResponseDTO<T> sucesso(String mensagem = "Operação realizada com sucesso", int codigo = 200) {
        return new ApiResponseDTO<T>(
                codigo: codigo,
                mensagem: mensagem,
                tipo: "Sucesso"
        )
    }

    static <T> ApiResponseDTO<T> erro(String mensagem, int codigo = 400, List<Object> erros = null) {
        return new ApiResponseDTO<T>(
                codigo: codigo,
                mensagem: mensagem,
                tipo: "Erro",
                erros: erros
        )
    }
}
