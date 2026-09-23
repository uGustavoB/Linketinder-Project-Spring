package org.ugustavodev.msnucleo.exception.handler

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.ugustavodev.msnucleo.dto.ApiResponseDTO
import org.ugustavodev.msnucleo.dto.exception.UnprocessableEntityExceptionDTO

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
                mensagem: ex.message,
                tipo: "Recurso Não Encontrado"
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta)
    }

    @ExceptionHandler(IllegalArgumentException)
    ResponseEntity<ApiResponseDTO<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiResponseDTO<Object> resposta = new ApiResponseDTO<>(
                codigo: HttpStatus.BAD_REQUEST.value(),
                mensagem: ex.message,
                tipo: "Requisição Inválida"
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta)
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    ResponseEntity<ApiResponseDTO<Object>> handleAuthenticationException(org.springframework.security.core.AuthenticationException ex) {
        ApiResponseDTO<Object> resposta = new ApiResponseDTO<>(
                codigo: HttpStatus.UNAUTHORIZED.value(),
                mensagem: "Credenciais inválidas ou usuário não encontrado.",
                tipo: "Não Autorizado"
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resposta)
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    ResponseEntity<ApiResponseDTO<Object>> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException ex) {
        ApiResponseDTO<Object> resposta = new ApiResponseDTO<>(
                codigo: HttpStatus.FORBIDDEN.value(),
                mensagem: "Você não tem permissão para acessar este recurso.",
                tipo: "Acesso Negado"
        )
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(resposta)
    }

    @ExceptionHandler(Exception)
    ResponseEntity<ApiResponseDTO<Object>> handleException(Exception ex) {
        ApiResponseDTO<Object> resposta = new ApiResponseDTO<>(
                codigo: HttpStatus.INTERNAL_SERVER_ERROR.value(),
                mensagem: "Ocorreu um erro: ${ex.message}",
                tipo: "Erro Interno"
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta)
    }
}
