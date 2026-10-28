package org.ugustavodev.msnucleo.usecase.impl.empresas

import org.springframework.security.crypto.password.PasswordEncoder
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.dto.request.EmpresaRequestDTO
import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO
import org.ugustavodev.msnucleo.mapper.EmpresaMapper
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.repositories.UsuarioRepository
import spock.lang.Specification
import spock.lang.Subject

class CadastrarEmpresaUseCaseSpec extends Specification {

    EmpresaRepository empresaRepository = Mock(EmpresaRepository)
    PasswordEncoder passwordEncoder = Mock(PasswordEncoder)
    EmpresaMapper empresaMapper = Mock(EmpresaMapper)
    UsuarioRepository usuarioRepository = Mock(UsuarioRepository)

    @Subject
    CadastrarEmpresaUseCase useCase = new CadastrarEmpresaUseCase(empresaRepository, passwordEncoder, empresaMapper, usuarioRepository)

    void "Deve lancar excecao quando requestDTO for nulo"() {
        given: "Um request nulo"
        EmpresaRequestDTO request = null

        when: "Tenta cadastrar empresa com request nulo"
        useCase.executar(request)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "O objeto EmpresaRequestDTO não pode ser nulo."
    }

    void "Deve lancar excecao quando a senha for nula ou menor que 6 caracteres"() {
        given: "Um request com senha curta"
        EmpresaRequestDTO request = new EmpresaRequestDTO(email: "rh@empresa.com", cnpj: "12345678000199", senha: "123")

        when: "Tenta cadastrar"
        useCase.executar(request)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "A senha é obrigatória e deve ter no mínimo 6 caracteres."
    }

    void "Deve lancar excecao quando o email ja estiver cadastrado"() {
        given: "Um request com email ja existente"
        EmpresaRequestDTO request = new EmpresaRequestDTO(email: "rh@empresa.com", cnpj: "12345678000199", senha: "senha123")
        1 * usuarioRepository.findByEmail("rh@empresa.com") >> Optional.of(new Usuario())

        when: "Tenta cadastrar"
        useCase.executar(request)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "Já existe um usuário cadastrado com o email informado"
    }

    void "Deve lancar excecao quando o CNPJ ja estiver cadastrado"() {
        given: "Um request com email unico mas CNPJ duplicado"
        EmpresaRequestDTO request = new EmpresaRequestDTO(email: "novo@empresa.com", cnpj: "12345678000199", senha: "senha123")
        1 * usuarioRepository.findByEmail("novo@empresa.com") >> Optional.empty()
        1 * empresaRepository.findByCnpj("12345678000199") >> Optional.of(new Empresa())

        when: "Tenta cadastrar"
        useCase.executar(request)

        then: "Lanca IllegalArgumentException"
        IllegalArgumentException ex = thrown(IllegalArgumentException)
        ex.message == "Já existe uma empresa cadastrada com o CNPJ informado"
    }

    void "Deve criptografar a senha e salvar a empresa com sucesso"() {
        given: "Um request valido de empresa"
        EmpresaRequestDTO request = new EmpresaRequestDTO(
                nome: "Tech Solutions Ltda",
                cnpj: "12345678000199",
                email: "rh@techsolutions.com.br",
                senha: "senha_plana",
                pais: "Brasil",
                estado: "SP",
                cep: "04538132",
                descricao: "Consultoria TI"
        )
        Empresa empresaMapeada = new Empresa(usuario: new Usuario(email: "rh@techsolutions.com.br", senha: "senha_plana"))
        Empresa empresaSalva = new Empresa(id: 1L, nome: "Tech Solutions Ltda", usuario: empresaMapeada.usuario)
        EmpresaResponseDTO esperado = new EmpresaResponseDTO(
                1L, "Tech Solutions Ltda", "12345678000199", "rh@techsolutions.com.br",
                "Brasil", "SP", "04538132", "Consultoria TI"
        )

        1 * usuarioRepository.findByEmail("rh@techsolutions.com.br") >> Optional.empty()
        1 * empresaRepository.findByCnpj("12345678000199") >> Optional.empty()
        1 * empresaMapper.fromRequestDTOToEntity(request) >> empresaMapeada
        1 * passwordEncoder.encode("senha_plana") >> "senha_criptografada"
        1 * empresaRepository.save(empresaMapeada) >> empresaSalva
        1 * empresaMapper.toResponseDTO(empresaSalva) >> esperado

        when: "Executa o cadastro da empresa"
        EmpresaResponseDTO resultado = useCase.executar(request)

        then: "A senha foi criptografada e o DTO retornado e o esperado"
        empresaMapeada.usuario.senha == "senha_criptografada"
        resultado == esperado
    }
}
