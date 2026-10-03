package org.ugustavodev.msnucleo.dto.request

import groovy.transform.Canonical
import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

@Canonical
class FormacaoRequestDTO {
    @NotBlank(message = "Instituição é obrigatória")
    String instituicao

    @NotBlank(message = "Curso é obrigatório")
    String curso

    @NotBlank(message = "Nível é obrigatório")
    String nivel

    @NotBlank(message = "Status é obrigatório")
    String status

    LocalDate dataInicio
    LocalDate dataConclusao
}
