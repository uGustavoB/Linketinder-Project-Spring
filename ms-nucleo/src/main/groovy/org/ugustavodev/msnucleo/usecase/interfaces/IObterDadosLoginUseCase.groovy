package org.ugustavodev.msnucleo.usecase.interfaces

import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.dto.response.LoginResponseDTO

interface IObterDadosLoginUseCase {

    LoginResponseDTO executar(Usuario usuario)

}
