package org.ugustavodev.mscompetencia.exception.handler

import jakarta.validation.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.validation.FieldError
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException
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

    @ExceptionHandler(ConstraintViolationException)
    ResponseEntity<ApiResponseDTO<Object>> handleConstraintViolationException(ConstraintViolationException e) {
        List<Object> erros = new ArrayList<>()

        e.constraintViolations.each { violation ->
            String path = violation.propertyPath?.toString() ?: ""
            String campo = path.contains(".") ? path.substring(path.lastIndexOf(".") + 1) : path
            erros.add(new UnprocessableEntityExceptionDTO(campo, violation.message))
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

    @ExceptionHandler(HandlerMethodValidationException)
    ResponseEntity<ApiResponseDTO<Object>> handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        List<Object> erros = new ArrayList<>()

        e.allErrors.each { error ->
            String campo = (error instanceof FieldError) ? error.field : "request"
            erros.add(new UnprocessableEntityExceptionDTO(campo, error.defaultMessage))
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

    @ExceptionHandler(NoResourceFoundException)
    ResponseEntity<ApiResponseDTO<Object>> handleNoResourceFoundException() {
        ApiResponseDTO<Object> resposta = new ApiResponseDTO<>(
                codigo: HttpStatus.NOT_FOUND.value(),
                status: HttpStatus.NOT_FOUND.value(),
                mensagem: "Endpoint não encontrado.",
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

    @ExceptionHandler([HttpMessageNotReadableException, MethodArgumentTypeMismatchException, MissingServletRequestParameterException])
    ResponseEntity<ApiResponseDTO<Object>> handleBadRequestExceptions() {
        ApiResponseDTO<Object> resposta = new ApiResponseDTO<>(
                codigo: HttpStatus.BAD_REQUEST.value(),
                status: HttpStatus.BAD_REQUEST.value(),
                mensagem: "Corpo da requisição ou parâmetros inválidos.",
                tipo: "Requisição Inválida"
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException)
    ResponseEntity<ApiResponseDTO<Object>> handleMethodNotSupportedException() {
        ApiResponseDTO<Object> resposta = new ApiResponseDTO<>(
                codigo: HttpStatus.METHOD_NOT_ALLOWED.value(),
                status: HttpStatus.METHOD_NOT_ALLOWED.value(),
                mensagem: "Método HTTP não suportado para este endpoint.",
                tipo: "Método Não Permitido"
        )
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(resposta)
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

    @ExceptionHandler(DataIntegrityViolationException)
    ResponseEntity<ApiResponseDTO<Object>> handleDataIntegrityViolationException() {
        ApiResponseDTO<Object> resposta = new ApiResponseDTO<>(
                codigo: HttpStatus.CONFLICT.value(),
                status: HttpStatus.CONFLICT.value(),
                mensagem: "Operação inválida: violação de integridade ou registro duplicado.",
                tipo: "Conflito de Dados"
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
