package org.ugustavodev.mscompetencia.dto
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class ApiResponseDTO<T> {
    T dados
    String mensagem
    int status

    static <T> ApiResponseDTO<T> sucesso(T dados, String mensagem = "Operação realizada com sucesso", int status = 200) {
        return new ApiResponseDTO<>(dados: dados, mensagem: mensagem, status: status)
    }

    static <T> ApiResponseDTO<T> sucesso(String mensagem, int status = 200) {
        return new ApiResponseDTO<>(mensagem: mensagem, status: status)
    }
}
