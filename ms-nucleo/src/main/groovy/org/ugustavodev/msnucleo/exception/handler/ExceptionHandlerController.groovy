package org.ugustavodev.msnucleo.exception.handler

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.ugustavodev.msnucleo.dto.ErroResponseDTO
import org.ugustavodev.msnucleo.dto.exception.UnprocessableEntityExceptionDTO

@RestControllerAdvice
class ExceptionHandlerController {

    @ExceptionHandler(MethodArgumentNotValidException)
    ResponseEntity<ErroResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<Object> erros = new ArrayList<>()

        e.bindingResult.fieldErrors.each { error ->
            erros.add(new UnprocessableEntityExceptionDTO(error.field, error.defaultMessage))
        }

        ErroResponseDTO resposta = new ErroResponseDTO(
                codigo: HttpStatus.UNPROCESSABLE_ENTITY.value(),
                mensagem: "Verifique os campos e tente novamente.",
                tipo: "Erro de Validação",
                erros: erros
        )

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(resposta)
    }

    @ExceptionHandler(IllegalArgumentException)
    ResponseEntity<ErroResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErroResponseDTO resposta = new ErroResponseDTO(
                codigo: HttpStatus.BAD_REQUEST.value(),
                mensagem: ex.message,
                tipo: "Requisição Inválida"
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta)
    }

    @ExceptionHandler(Exception)
    ResponseEntity<ErroResponseDTO> handleException(Exception ex) {
        ErroResponseDTO resposta = new ErroResponseDTO(
                codigo: HttpStatus.INTERNAL_SERVER_ERROR.value(),
                mensagem: "Ocorreu um erro: ${ex.message}",
                tipo: "Erro Interno"
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta)
    }
}
