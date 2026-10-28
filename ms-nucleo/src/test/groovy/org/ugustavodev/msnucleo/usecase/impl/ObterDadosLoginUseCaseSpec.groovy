package org.ugustavodev.msnucleo.usecase.impl

import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.dto.response.LoginResponseDTO
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import spock.lang.Specification
import spock.lang.Subject

class ObterDadosLoginUseCaseSpec extends Specification {

    CandidatoRepository candidatoRepository = Mock(CandidatoRepository)
    EmpresaRepository empresaRepository = Mock(EmpresaRepository)

    @Subject
    ObterDadosLoginUseCase useCase = new ObterDadosLoginUseCase(candidatoRepository, empresaRepository)

    void "Deve retornar os dados de login preenchidos para usuario do tipo CANDIDATO"() {
        given: "Um usuario autenticado com papel CANDIDATO"
        Usuario usuario = new Usuario(id: 1L, email: "joao.silva@email.com", role: Role.CANDIDATO)
        Candidato candidato = new Candidato(id: 10L, nome: "João", sobrenome: "Silva", usuario: usuario)

        and: "O repositorio de candidatos encontra o registro pelo email"
        1 * candidatoRepository.findByUsuarioEmail("joao.silva@email.com") >> Optional.of(candidato)
        0 * empresaRepository.findByUsuarioEmail(_)

        when: "Executa a obtencao dos dados de login"
        LoginResponseDTO resultado = useCase.executar(usuario)

        then: "Retorna o DTO com id, nome, sobrenome, email e tipoUsuario"
        resultado != null
        resultado.id == 10L
        resultado.nome == "João"
        resultado.sobrenome == "Silva"
        resultado.email == "joao.silva@email.com"
        resultado.tipoUsuario == "CANDIDATO"
    }

    void "Deve retornar os dados de login preenchidos para usuario do tipo EMPRESA"() {
        given: "Um usuario autenticado com papel EMPRESA"
        Usuario usuario = new Usuario(id: 2L, email: "rh@techsolutions.com.br", role: Role.EMPRESA)
        Empresa empresa = new Empresa(id: 20L, nome: "Tech Solutions Ltda", usuario: usuario)

        and: "O repositorio de empresas encontra o registro pelo email"
        1 * empresaRepository.findByUsuarioEmail("rh@techsolutions.com.br") >> Optional.of(empresa)
        0 * candidatoRepository.findByUsuarioEmail(_)

        when: "Executa a obtencao dos dados de login"
        LoginResponseDTO resultado = useCase.executar(usuario)

        then: "Retorna o DTO com id, nome, email e tipoUsuario sem sobrenome"
        resultado != null
        resultado.id == 20L
        resultado.nome == "Tech Solutions Ltda"
        resultado.sobrenome == null
        resultado.email == "rh@techsolutions.com.br"
        resultado.tipoUsuario == "EMPRESA"
    }

}
