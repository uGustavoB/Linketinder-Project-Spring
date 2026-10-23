package org.ugustavodev.mscompetencia.exception.handler

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.ugustavodev.mscompetencia.dto.ApiResponseDTO
import org.ugustavodev.mscompetencia.dto.exception.UnprocessableEntityExceptionDTO

@RestControllerAdvice
class ExceptionHandlerController {
    @ExceptionHandler(MethodArgumentNotValidException)
    ResponseEntity<ApiResponseDTO<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<Object> erros = new ArrayList<>()

        e.bindingResult.fieldErrors.each { error ->
            erros.add(new UnprocessableEntityExceptionDTO(error.field, error.defaultMessage))
        }

        ApiResponseDTO<Object> resposta = new ApiResponseDTO<>(
                codigo: HttpStatus.UNPROCESSABLE_ENTITY.value(),
                status: HttpStatus.UNPROCESSABLE_ENTITY.value(),
                mensagem: "Verifique os campos e tente novamente.",
                tipo: "Erro de Validação",
                erros: erros
        )

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(resposta)
    }

    @ExceptionHandler(NoSuchElementException)
    ResponseEntity<ApiResponseDTO<Object>> handleNoSuchElementException(NoSuchElementException ex) {
        ApiResponseDTO<Object> resposta = new ApiResponseDTO<>(
                codigo: HttpStatus.NOT_FOUND.value(),
                status: HttpStatus.NOT_FOUND.value(),
                mensagem: ex.message,
                tipo: "Recurso Não Encontrado"
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta)
    }

    @ExceptionHandler(IllegalArgumentException)
    ResponseEntity<ApiResponseDTO<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiResponseDTO<Object> resposta = new ApiResponseDTO<>(
                codigo: HttpStatus.BAD_REQUEST.value(),
                status: HttpStatus.BAD_REQUEST.value(),
                mensagem: ex.message,
                tipo: "Requisição Inválida"
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta)
    }

    @ExceptionHandler(IllegalStateException)
    ResponseEntity<ApiResponseDTO<Object>> handleIllegalStateException(IllegalStateException ex) {
        ApiResponseDTO<Object> resposta = new ApiResponseDTO<>(
                codigo: HttpStatus.CONFLICT.value(),
                status: HttpStatus.CONFLICT.value(),
                mensagem: ex.message,
                tipo: "Conflito"
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(resposta)
    }

    @ExceptionHandler(AccessDeniedException)
    ResponseEntity<ApiResponseDTO<Object>> handleAccessDeniedException() {
        ApiResponseDTO<Object> resposta = new ApiResponseDTO<>(
                codigo: HttpStatus.FORBIDDEN.value(),
                status: HttpStatus.FORBIDDEN.value(),
                mensagem: "Você não tem permissão para acessar este recurso.",
                tipo: "Acesso Negado"
        )
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(resposta)
    }

    @ExceptionHandler(Exception)
    ResponseEntity<ApiResponseDTO<Object>> handleException() {
        ApiResponseDTO<Object> resposta = new ApiResponseDTO<>(
                codigo: HttpStatus.INTERNAL_SERVER_ERROR.value(),
                status: HttpStatus.INTERNAL_SERVER_ERROR.value(),
                mensagem: "Ocorreu um erro interno no servidor.",
                tipo: "Erro Interno"
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta)
    }
}
