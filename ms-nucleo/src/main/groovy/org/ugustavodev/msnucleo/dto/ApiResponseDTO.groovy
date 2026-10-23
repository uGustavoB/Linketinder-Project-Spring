package org.ugustavodev.msnucleo.dto

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Envelope padrão de resposta HTTP da API do LinkeTinder")
class ApiResponseDTO<T> {
    @Schema(description = "Código de status HTTP da resposta", example = "200")
    int codigo

    @Schema(description = "Mensagem amigável descrevendo o resultado da operação", example = "Operação realizada com sucesso")
    String mensagem

    @Schema(description = "Categoria do retorno (Sucesso, Erro, Erro de Validação, Recurso Não Encontrado, etc.)", example = "Sucesso")
    String tipo

    @Schema(description = "Carga útil (payload) retornada em operações de sucesso")
    T dados

    @Schema(description = "Lista detalhada de erros de validação (presente apenas em falhas HTTP 422)")
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
