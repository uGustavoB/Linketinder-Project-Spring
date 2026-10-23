package org.ugustavodev.msnucleo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.dto.ApiResponseDTO
import org.ugustavodev.msnucleo.usecase.interfaces.IObterPerfilUseCase

@RestController
@RequestMapping("/api/perfil")
@Tag(name = "2. Perfil", description = "Consulta dos dados cadastrais completos do usuário autenticado (Candidato ou Empresa)")
class PerfilController {

    private final IObterPerfilUseCase obterPerfilUseCase

    PerfilController(IObterPerfilUseCase obterPerfilUseCase) {
        this.obterPerfilUseCase = obterPerfilUseCase
    }

    @Operation(
            summary = "Obtém o perfil completo do usuário autenticado",
            description = "Retorna `PerfilCandidatoDTO` caso o usuário autenticado possua `ROLE_CANDIDATO`, ou `PerfilEmpresaDTO` caso possua `ROLE_EMPRESA`."
    )
    @ApiResponses([
            @ApiResponse(responseCode = "200", description = "Perfil obtido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    ])
    @GetMapping
    ResponseEntity<ApiResponseDTO<Object>> getPerfil() {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal()

        Object dto = obterPerfilUseCase.executar(usuario)
        
        if (dto != null) {
            return ResponseEntity.ok(ApiResponseDTO.sucesso(dto, "Perfil obtido com sucesso"))
        }

        throw new NoSuchElementException("Perfil não encontrado")
    }
}
