package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.security.crypto.password.PasswordEncoder
import org.ugustavodev.msnucleo.dto.request.CandidatoRequestDTO
import org.ugustavodev.msnucleo.dto.response.CandidatoResponseDTO
import org.ugustavodev.msnucleo.mapper.CandidatoMapper
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.repositories.UsuarioRepository
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

class CadastrarCandidatoUseCaseTest extends Specification {

    CandidatoRepository candidatoRepository = Mock(CandidatoRepository)
    PasswordEncoder passwordEncoder = Mock(PasswordEncoder)
    CandidatoMapper candidatoMapper = Mock(CandidatoMapper)
    UsuarioRepository usuarioRepository = Mock(UsuarioRepository)

    @Subject
    CadastrarCandidatoUseCase useCase = new CadastrarCandidatoUseCase(candidatoRepository, passwordEncoder, candidatoMapper, usuarioRepository)

    void "Deve lancar excecao quando request for nulo"() {
        when: "Tenta executar o caso de uso com request nulo"
        useCase.executar(null)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O objeto CandidatoRequestDTO não pode ser nulo."
    }

    void "Deve lancar excecao quando email ja estiver cadastrado"() {
        given: "Um request de candidato valido"
        CandidatoRequestDTO request = new CandidatoRequestDTO(email: "teste@teste.com", cpf: "111")

        and: "O repositorio retorna que ja existe um usuario com esse email"
        usuarioRepository.findByEmail("teste@teste.com") >> Optional.of(new Usuario())

        when: "Tenta cadastrar"
        useCase.executar(request)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "Já existe um usuário cadastrado com o email informado"
    }

    void "Deve lancar excecao quando CPF ja estiver cadastrado"() {
        given: "Um request de candidato com email unico, mas CPF repetido"
        CandidatoRequestDTO request = new CandidatoRequestDTO(email: "unico@teste.com", cpf: "12345678900")

        and: "O email nao existe"
        usuarioRepository.findByEmail("unico@teste.com") >> Optional.empty()

        and: "O CPF ja existe"
        candidatoRepository.findByCpf("12345678900") >> Optional.of(new Candidato())

        when: "Tenta cadastrar"
        useCase.executar(request)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "Já existe um candidato cadastrado com o CPF informado"
    }

    void "Deve criptografar a senha do usuario antes de salvar"() {
        given: "Um request valido"
        CandidatoRequestDTO request = new CandidatoRequestDTO(email: "novo@teste.com", cpf: "111", senha: "senha_plana")

        and: "Nenhum email ou CPF duplicado"
        usuarioRepository.findByEmail(_ as String) >> Optional.empty()
        candidatoRepository.findByCpf(_ as String) >> Optional.empty()

        and: "O mapper retorna a entidade com a senha plana"
        Candidato candidatoMapeado = new Candidato(usuario: new Usuario(senha: "senha_plana"))
        candidatoMapper.fromRequestDTOToEntity(request) >> candidatoMapeado

        when: "Executa o caso de uso"
        useCase.executar(request)

        then: "O password encoder deve ser chamado para a senha plana"
        1 * passwordEncoder.encode("senha_plana") >> "senha_criptografada"
        
        and: "A senha da entidade deve ser atualizada"
        candidatoMapeado.usuario.senha == "senha_criptografada"
    }

    void "Deve salvar o candidato com sucesso e retornar DTO"() {
        given: "Um request valido"
        CandidatoRequestDTO request = new CandidatoRequestDTO(email: "sucesso@teste.com", cpf: "123")

        and: "Mock das dependencias para sucesso"
        usuarioRepository.findByEmail(_ as String) >> Optional.empty()
        candidatoRepository.findByCpf(_ as String) >> Optional.empty()
        
        Candidato candidatoMock = new Candidato(usuario: new Usuario(senha: "123"))
        candidatoMapper.fromRequestDTOToEntity(request) >> candidatoMock
        
        passwordEncoder.encode(_ as CharSequence) >> "hash"
        
        Candidato candidatoSalvo = new Candidato(id: 1L)
        candidatoRepository.save(candidatoMock) >> candidatoSalvo
        
        CandidatoResponseDTO responseDTO = new CandidatoResponseDTO(1L, "Nome", "Sobrenome", LocalDate.now(), "sucesso@teste.com", "123", "BR", "SP", "000", "Dev")
        candidatoMapper.toResponseDTO(candidatoSalvo) >> responseDTO

        when: "Executa o caso de uso"
        CandidatoResponseDTO resultado = useCase.executar(request)

        then: "O DTO retornado e o esperado"
        resultado != null
        resultado.id() == 1L
        resultado.email() == "sucesso@teste.com"
    }
}
