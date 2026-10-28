package org.ugustavodev.msnucleo.usecase.impl

import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.dto.response.PerfilCandidatoDTO
import org.ugustavodev.msnucleo.dto.response.PerfilEmpresaDTO
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

class ObterPerfilUseCaseSpec extends Specification {

    CandidatoRepository candidatoRepository = Mock(CandidatoRepository)
    EmpresaRepository empresaRepository = Mock(EmpresaRepository)

    @Subject
    ObterPerfilUseCase useCase = new ObterPerfilUseCase(candidatoRepository, empresaRepository)

    void "Deve retornar PerfilCandidatoDTO quando o usuario autenticado for CANDIDATO"() {
        given: "Um usuario autenticado com papel CANDIDATO"
        LocalDate dataNascimento = LocalDate.of(1998, 5, 15)
        Usuario usuario = new Usuario(id: 1L, email: "joao.silva@email.com", role: Role.CANDIDATO)
        Candidato candidato = new Candidato(
                id: 10L,
                nome: "João",
                sobrenome: "Silva",
                dataNascimento: dataNascimento,
                cpf: "12345678901",
                pais: "Brasil",
                estado: "SP",
                cep: "01001000",
                descricao: "Desenvolvedor Backend",
                usuario: usuario
        )

        and: "O repositorio de candidatos retorna o candidato"
        1 * candidatoRepository.findByUsuarioEmail("joao.silva@email.com") >> Optional.of(candidato)

        when: "Executa a consulta de perfil"
        Object resultado = useCase.executar(usuario)

        then: "Retorna uma instancia de PerfilCandidatoDTO com todos os dados cadastrais"
        resultado instanceof PerfilCandidatoDTO
        PerfilCandidatoDTO perfil = (PerfilCandidatoDTO) resultado
        perfil.id == 10L
        perfil.tipoUsuario == "CANDIDATO"
        perfil.nome == "João"
        perfil.sobrenome == "Silva"
        perfil.dataNascimento == dataNascimento
        perfil.email == "joao.silva@email.com"
        perfil.cpf == "12345678901"
        perfil.pais == "Brasil"
        perfil.estado == "SP"
        perfil.cep == "01001000"
        perfil.descricao == "Desenvolvedor Backend"
    }

    void "Deve retornar PerfilEmpresaDTO quando o usuario autenticado for EMPRESA"() {
        given: "Um usuario autenticado com papel EMPRESA"
        Usuario usuario = new Usuario(id: 2L, email: "rh@techsolutions.com.br", role: Role.EMPRESA)
        Empresa empresa = new Empresa(
                id: 20L,
                nome: "Tech Solutions Ltda",
                cnpj: "12345678000199",
                pais: "Brasil",
                estado: "SP",
                cep: "04538132",
                descricao: "Consultoria em TI",
                usuario: usuario
        )

        and: "O repositorio de empresas retorna a empresa"
        1 * empresaRepository.findByUsuarioEmail("rh@techsolutions.com.br") >> Optional.of(empresa)

        when: "Executa a consulta de perfil"
        Object resultado = useCase.executar(usuario)

        then: "Retorna uma instancia de PerfilEmpresaDTO com todos os dados corporativos"
        resultado instanceof PerfilEmpresaDTO
        PerfilEmpresaDTO perfil = (PerfilEmpresaDTO) resultado
        perfil.id == 20L
        perfil.tipoUsuario == "EMPRESA"
        perfil.nome == "Tech Solutions Ltda"
        perfil.cnpj == "12345678000199"
        perfil.email == "rh@techsolutions.com.br"
        perfil.pais == "Brasil"
        perfil.estado == "SP"
        perfil.cep == "04538132"
        perfil.descricao == "Consultoria em TI"
    }

    void "Deve retornar nulo quando o perfil do usuario nao for encontrado"() {
        given: "Um usuario cujo registro de candidato nao existe"
        Usuario usuario = new Usuario(id: 99L, email: "inexistente@email.com", role: Role.CANDIDATO)
        1 * candidatoRepository.findByUsuarioEmail("inexistente@email.com") >> Optional.empty()

        when: "Executa a consulta de perfil"
        Object resultado = useCase.executar(usuario)

        then: "Retorna nulo"
        resultado == null
    }
}
