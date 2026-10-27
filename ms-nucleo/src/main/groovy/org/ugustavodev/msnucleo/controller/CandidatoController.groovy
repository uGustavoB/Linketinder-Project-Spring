package org.ugustavodev.msnucleo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.dto.request.CandidatoRequestDTO
import org.ugustavodev.msnucleo.dto.response.CandidatoResponseDTO
import org.ugustavodev.msnucleo.dto.response.CandidatoPublicoResponseDTO
import org.ugustavodev.msnucleo.dto.response.MatchCandidatoResponseDTO
import org.ugustavodev.msnucleo.dto.ApiResponseDTO
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.usecase.interfaces.candidatos.IAtualizarCandidatoUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.candidatos.IBuscarCandidatoPorIdUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.candidatos.ICadastrarCandidatoUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.candidatos.IDeletarCandidatoUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.candidatos.IListarCandidatosUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.matches.IListarMatchesCandidatoUseCase

@RestController
@RequestMapping("/api/candidato")
@Tag(name = "3. Candidatos", description = "Gestão cadastral de candidatos, listagem pública anonimizada e consulta de matches do candidato")
class CandidatoController {
    private final ICadastrarCandidatoUseCase cadastrarCandidatoUseCase
    private final IBuscarCandidatoPorIdUseCase buscarCandidatoPorIdUseCase
    private final IListarCandidatosUseCase listarCandidatosUseCase
    private final IAtualizarCandidatoUseCase atualizarCandidatoUseCase
    private final IDeletarCandidatoUseCase deletarCandidatoUseCase
    private final IListarMatchesCandidatoUseCase listarMatchesCandidatoUseCase
    private final CandidatoRepository candidatoRepository

    CandidatoController(
            ICadastrarCandidatoUseCase cadastrarCandidatoUseCase,
            IBuscarCandidatoPorIdUseCase buscarCandidatoPorIdUseCase,
            IListarCandidatosUseCase listarCandidatosUseCase,
            IAtualizarCandidatoUseCase atualizarCandidatoUseCase,
            IDeletarCandidatoUseCase deletarCandidatoUseCase,
            IListarMatchesCandidatoUseCase listarMatchesCandidatoUseCase,
            CandidatoRepository candidatoRepository
    ) {
        this.cadastrarCandidatoUseCase = cadastrarCandidatoUseCase
        this.buscarCandidatoPorIdUseCase = buscarCandidatoPorIdUseCase
        this.listarCandidatosUseCase = listarCandidatosUseCase
        this.atualizarCandidatoUseCase = atualizarCandidatoUseCase
        this.deletarCandidatoUseCase = deletarCandidatoUseCase
        this.listarMatchesCandidatoUseCase = listarMatchesCandidatoUseCase
        this.candidatoRepository = candidatoRepository
    }

    @Operation(
            summary = "Cadastra um novo candidato (Público)",
            description = "Cria uma nova conta de candidato com perfil `ROLE_CANDIDATO`. O e-mail e o CPF devem ser únicos no sistema."
    )
    @SecurityRequirements
    @ApiResponses([
            @ApiResponse(
                    responseCode = "201",
                    description = "Candidato cadastrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Candidato Cadastrado",
                                    value = """{
                                        "codigo": 201,
                                        "mensagem": "Candidato cadastrado com sucesso",
                                        "tipo": "Sucesso",
                                        "dados": {
                                            "id": 1,
                                            "nome": "João",
                                            "sobrenome": "Silva",
                                            "dataNascimento": "1998-05-15",
                                            "email": "joao.silva@email.com",
                                            "cpf": "12345678901",
                                            "pais": "Brasil",
                                            "estado": "SP",
                                            "cep": "01001000",
                                            "descricao": "Desenvolvedor Backend com foco em Java, Groovy e Spring Boot.",
                                            "formacoes": []
                                        }
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "E-mail/CPF já cadastrado ou senha inválida",
                    content = @Content(
                            mediaType = "application/json",
                            examples = [
                                    @ExampleObject(
                                            name = "E-mail Duplicado",
                                            value = """{
                                                "codigo": 400,
                                                "mensagem": "Já existe um usuário cadastrado com o email informado",
                                                "tipo": "Requisição Inválida"
                                            }"""
                                    ),
                                    @ExampleObject(
                                            name = "CPF Duplicado",
                                            value = """{
                                                "codigo": 400,
                                                "mensagem": "Já existe um candidato cadastrado com o CPF informado",
                                                "tipo": "Requisição Inválida"
                                            }"""
                                    )
                            ]
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Erro de validação nos campos informados",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Erro de Validação",
                                    value = """{
                                        "codigo": 422,
                                        "mensagem": "Verifique os campos e tente novamente.",
                                        "tipo": "Erro de Validação",
                                        "erros": [
                                            {
                                                "campo": "cpf",
                                                "mensagem": "O CPF deve ter 11 dígitos"
                                            },
                                            {
                                                "campo": "email",
                                                "mensagem": "O campo email deve ser um email válido"
                                            }
                                        ]
                                    }"""
                            )
                    )
            )
    ])
    @PostMapping
    ResponseEntity<ApiResponseDTO<CandidatoResponseDTO>> cadastrarCandidato(@Valid @RequestBody CandidatoRequestDTO requestDTO) {
        CandidatoResponseDTO responseDTO = cadastrarCandidatoUseCase.executar(requestDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.sucesso(responseDTO, "Candidato cadastrado com sucesso", HttpStatus.CREATED.value()))
    }

    @Operation(
            summary = "Lista candidatos de forma anonimizada (Pré-Match)",
            description = "Retorna `CandidatoPublicoResponseDTO` contendo apenas ID, país, estado, descrição e formações acadêmicas. Oculta nome, sobrenome, e-mail, CPF, data de nascimento e CEP em respeito ao Princípio do Anonimato."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de candidatos anônimos retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Lista Anonimizada de Candidatos",
                                    value = """{
                                        "codigo": 200,
                                        "mensagem": "Operação realizada com sucesso",
                                        "tipo": "Sucesso",
                                        "dados": [
                                            {
                                                "id": 1,
                                                "pais": "Brasil",
                                                "estado": "SP",
                                                "descricao": "Desenvolvedor Backend com foco em Java, Groovy e Spring Boot.",
                                                "formacoes": [
                                                    {
                                                        "id": 1,
                                                        "instituicao": "Universidade de São Paulo (USP)",
                                                        "curso": "Ciência da Computação",
                                                        "nivel": "Bacharelado",
                                                        "status": "Concluído",
                                                        "dataInicio": "2020-02-01",
                                                        "dataConclusao": "2023-12-15"
                                                    }
                                                ]
                                            }
                                        ]
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Não Autenticado",
                                    value = """{
                                        "codigo": 401,
                                        "mensagem": "Você não está autenticado. Informe um token JWT válido.",
                                        "tipo": "Não Autorizado"
                                    }"""
                            )
                    )
            )
    ])
    @GetMapping
    ResponseEntity<ApiResponseDTO<List<CandidatoPublicoResponseDTO>>> listarCandidatos() {
        List<CandidatoPublicoResponseDTO> lista = listarCandidatosUseCase.executar()
        return ResponseEntity.ok(ApiResponseDTO.sucesso(lista))
    }

    @Operation(
            summary = "Busca dados completos do candidato por ID (Restrito ao Titular)",
            description = "Retorna `CandidatoResponseDTO` com todos os dados pessoais. Bloqueia o acesso de empresas (`ROLE_EMPRESA`) e de outros candidatos com HTTP 403."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Dados do candidato retornados com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Candidato Completo (Titular)",
                                    value = """{
                                        "codigo": 200,
                                        "mensagem": "Operação realizada com sucesso",
                                        "tipo": "Sucesso",
                                        "dados": {
                                            "id": 1,
                                            "nome": "João",
                                            "sobrenome": "Silva",
                                            "dataNascimento": "1998-05-15",
                                            "email": "joao.silva@email.com",
                                            "cpf": "12345678901",
                                            "pais": "Brasil",
                                            "estado": "SP",
                                            "cep": "01001000",
                                            "descricao": "Desenvolvedor Backend com foco em Java, Groovy e Spring Boot.",
                                            "formacoes": [
                                                {
                                                    "id": 1,
                                                    "instituicao": "Universidade de São Paulo (USP)",
                                                    "curso": "Ciência da Computação",
                                                    "nivel": "Bacharelado",
                                                    "status": "Concluído",
                                                    "dataInicio": "2020-02-01",
                                                    "dataConclusao": "2023-12-15"
                                                }
                                            ]
                                        }
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Não Autenticado",
                                    value = """{
                                        "codigo": 401,
                                        "mensagem": "Você não está autenticado. Informe um token JWT válido.",
                                        "tipo": "Não Autorizado"
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado: empresas ou outros candidatos não podem visualizar dados sensíveis",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Acesso Negado",
                                    value = """{
                                        "codigo": 403,
                                        "mensagem": "Você não tem permissão para acessar este recurso.",
                                        "tipo": "Acesso Negado"
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Candidato não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Candidato Não Encontrado",
                                    value = """{
                                        "codigo": 404,
                                        "mensagem": "Candidato não encontrado.",
                                        "tipo": "Recurso Não Encontrado"
                                    }"""
                            )
                    )
            )
    ])
    @GetMapping("/{id}")
    ResponseEntity<ApiResponseDTO<CandidatoResponseDTO>> buscarCandidatoPorId(
            @Parameter(description = "ID do candidato", example = "1") @PathVariable("id") Long id
    ) {
        CandidatoResponseDTO responseDTO = buscarCandidatoPorIdUseCase.executar(id)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(responseDTO))
    }

    @Operation(
            summary = "Atualiza os dados cadastrais do próprio candidato",
            description = "Permite ao candidato titular atualizar seus dados pessoais e, opcionalmente, sua senha."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Candidato atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Candidato Atualizado",
                                    value = """{
                                        "codigo": 200,
                                        "mensagem": "Candidato atualizado com sucesso",
                                        "tipo": "Sucesso",
                                        "dados": {
                                            "id": 1,
                                            "nome": "João",
                                            "sobrenome": "Silva",
                                            "dataNascimento": "1998-05-15",
                                            "email": "joao.silva@email.com",
                                            "cpf": "12345678901",
                                            "pais": "Brasil",
                                            "estado": "SP",
                                            "cep": "01001000",
                                            "descricao": "Desenvolvedor Backend Sênior com foco em Java, Groovy e Spring Boot.",
                                            "formacoes": []
                                        }
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Conflito de e-mail/CPF ou dados inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Conflito de E-mail ou CPF",
                                    value = """{
                                        "codigo": 400,
                                        "mensagem": "Já existe outro candidato cadastrado com o email informado",
                                        "tipo": "Requisição Inválida"
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Não Autenticado",
                                    value = """{
                                        "codigo": 401,
                                        "mensagem": "Você não está autenticado. Informe um token JWT válido.",
                                        "tipo": "Não Autorizado"
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado para alterar perfil de terceiro",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Acesso Negado",
                                    value = """{
                                        "codigo": 403,
                                        "mensagem": "Você não tem permissão para acessar este recurso.",
                                        "tipo": "Acesso Negado"
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Candidato não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Candidato Não Encontrado",
                                    value = """{
                                        "codigo": 404,
                                        "mensagem": "Candidato não encontrado.",
                                        "tipo": "Recurso Não Encontrado"
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Erro de validação nos campos informados",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Erro de Validação",
                                    value = """{
                                        "codigo": 422,
                                        "mensagem": "Verifique os campos e tente novamente.",
                                        "tipo": "Erro de Validação",
                                        "erros": [
                                            {
                                                "campo": "nome",
                                                "mensagem": "O campo nome é obrigatório"
                                            }
                                        ]
                                    }"""
                            )
                    )
            )
    ])
    @PutMapping("/{id}")
    ResponseEntity<ApiResponseDTO<CandidatoResponseDTO>> atualizarCandidato(
            @Parameter(description = "ID do candidato", example = "1") @PathVariable("id") Long id,
            @Valid @RequestBody CandidatoRequestDTO requestDTO
    ) {
        CandidatoResponseDTO responseDTO = atualizarCandidatoUseCase.executar(id, requestDTO)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(responseDTO, "Candidato atualizado com sucesso"))
    }

    @Operation(
            summary = "Deleta a conta do próprio candidato",
            description = "Remove em cascata todas as curtidas, formações acadêmicas, perfil e credenciais de usuário do candidato titular."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Candidato deletado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Candidato Deletado",
                                    value = """{
                                        "codigo": 200,
                                        "mensagem": "Candidato deletado com sucesso",
                                        "tipo": "Sucesso"
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Não Autenticado",
                                    value = """{
                                        "codigo": 401,
                                        "mensagem": "Você não está autenticado. Informe um token JWT válido.",
                                        "tipo": "Não Autorizado"
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado para deletar perfil de terceiro",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Acesso Negado",
                                    value = """{
                                        "codigo": 403,
                                        "mensagem": "Você não tem permissão para acessar este recurso.",
                                        "tipo": "Acesso Negado"
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Candidato não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Candidato Não Encontrado",
                                    value = """{
                                        "codigo": 404,
                                        "mensagem": "Candidato não encontrado.",
                                        "tipo": "Recurso Não Encontrado"
                                    }"""
                            )
                    )
            )
    ])
    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponseDTO<Void>> deletarCandidato(
            @Parameter(description = "ID do candidato", example = "1") @PathVariable("id") Long id
    ) {
        deletarCandidatoUseCase.executar(id)
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Candidato deletado com sucesso", HttpStatus.OK.value()))
    }

    @Operation(
            summary = "Lista os matches consolidados do candidato autenticado (Pós-Match)",
            description = "Retorna todas as vagas em que houve interesse mútuo (`candidatoCurtiuEm != null` e `empresaCurtiuEm != null`), revelando o nome e o e-mail de contato da empresa contratante."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de matches retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Matches do Candidato",
                                    value = """{
                                        "codigo": 200,
                                        "mensagem": "Operação realizada com sucesso",
                                        "tipo": "Sucesso",
                                        "dados": [
                                            {
                                                "vaga": {
                                                    "id": 5,
                                                    "nome": "Pessoa Desenvolvedora Backend Groovy/Spring",
                                                    "descricao": "Atuação no desenvolvimento e manutenção de microsserviços RESTful com Spring Boot e PostgreSQL.",
                                                    "local": "São Paulo - SP (Remoto)",
                                                    "nivel": "Pleno",
                                                    "empresaId": 1
                                                },
                                                "empresaNome": "Tech Solutions Ltda",
                                                "empresaEmail": "rh@techsolutions.com.br",
                                                "empresaPais": "Brasil",
                                                "empresaEstado": "SP",
                                                "empresaDescricao": "Consultoria especializada em arquitetura de microsserviços e soluções em nuvem.",
                                                "dataMatch": "2026-10-23T09:15:00"
                                            }
                                        ]
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Não Autenticado",
                                    value = """{
                                        "codigo": 401,
                                        "mensagem": "Você não está autenticado. Informe um token JWT válido.",
                                        "tipo": "Não Autorizado"
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso restrito a usuários com papel CANDIDATO",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Acesso Negado",
                                    value = """{
                                        "codigo": 403,
                                        "mensagem": "Você não tem permissão para acessar este recurso.",
                                        "tipo": "Acesso Negado"
                                    }"""
                            )
                    )
            )
    ])
    @PreAuthorize("hasRole('CANDIDATO')")
    @GetMapping("/matches")
    ResponseEntity<ApiResponseDTO<List<MatchCandidatoResponseDTO>>> listarMatches(
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Candidato candidato = candidatoRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Candidato não encontrado"))
        Long idCandidato = candidato.getId()
        
        List<MatchCandidatoResponseDTO> matches = listarMatchesCandidatoUseCase.executar(idCandidato)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(matches))
    }
}
