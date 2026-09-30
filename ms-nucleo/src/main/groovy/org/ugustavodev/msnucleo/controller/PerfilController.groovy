package org.ugustavodev.msnucleo.controller

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
class PerfilController {

    private final IObterPerfilUseCase obterPerfilUseCase

    PerfilController(IObterPerfilUseCase obterPerfilUseCase) {
        this.obterPerfilUseCase = obterPerfilUseCase
    }

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
