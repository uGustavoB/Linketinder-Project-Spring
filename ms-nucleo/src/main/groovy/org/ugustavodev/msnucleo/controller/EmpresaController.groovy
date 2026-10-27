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
import org.ugustavodev.msnucleo.dto.request.EmpresaRequestDTO
import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO
import org.ugustavodev.msnucleo.dto.response.EmpresaPublicoResponseDTO
import org.ugustavodev.msnucleo.dto.response.MatchEmpresaResponseDTO
import org.ugustavodev.msnucleo.dto.ApiResponseDTO
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IAtualizarEmpresaUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IBuscarEmpresaPorIdUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.ICadastrarEmpresaUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IDeletarEmpresaUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IListarEmpresasUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IListarMatchesEmpresaUseCase

@RestController
@RequestMapping("/api/empresa")
@Tag(name = "5. Empresas", description = "Gestão cadastral de empresas, listagem pública anonimizada e consulta de matches da empresa")
class EmpresaController {
    private final ICadastrarEmpresaUseCase cadastrarEmpresaUseCase
    private final IBuscarEmpresaPorIdUseCase buscarEmpresaPorIdUseCase
    private final IListarEmpresasUseCase listarEmpresasUseCase
    private final IAtualizarEmpresaUseCase atualizarEmpresaUseCase
    private final IDeletarEmpresaUseCase deletarEmpresaUseCase
    private final IListarMatchesEmpresaUseCase listarMatchesEmpresaUseCase
    private final EmpresaRepository empresaRepository

    EmpresaController(
            ICadastrarEmpresaUseCase cadastrarEmpresaUseCase,
            IBuscarEmpresaPorIdUseCase buscarEmpresaPorIdUseCase,
            IListarEmpresasUseCase listarEmpresasUseCase,
            IAtualizarEmpresaUseCase atualizarEmpresaUseCase,
            IDeletarEmpresaUseCase deletarEmpresaUseCase,
            IListarMatchesEmpresaUseCase listarMatchesEmpresaUseCase,
            EmpresaRepository empresaRepository
    ) {
        this.cadastrarEmpresaUseCase = cadastrarEmpresaUseCase
        this.buscarEmpresaPorIdUseCase = buscarEmpresaPorIdUseCase
        this.listarEmpresasUseCase = listarEmpresasUseCase
        this.atualizarEmpresaUseCase = atualizarEmpresaUseCase
        this.deletarEmpresaUseCase = deletarEmpresaUseCase
        this.listarMatchesEmpresaUseCase = listarMatchesEmpresaUseCase
        this.empresaRepository = empresaRepository
    }

    @Operation(
            summary = "Cadastra uma nova empresa (Público)",
            description = "Cria uma nova conta corporativa com perfil `ROLE_EMPRESA`. O e-mail e o CNPJ devem ser únicos no sistema."
    )
    @SecurityRequirements
    @ApiResponses([
            @ApiResponse(
                    responseCode = "201",
                    description = "Empresa cadastrada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Empresa Cadastrada",
                                    value = """{
                                        "codigo": 201,
                                        "mensagem": "Empresa cadastrada com sucesso",
                                        "tipo": "Sucesso",
                                        "dados": {
                                            "id": 1,
                                            "nome": "Tech Solutions Ltda",
                                            "cnpj": "12345678000199",
                                            "email": "rh@techsolutions.com.br",
                                            "pais": "Brasil",
                                            "estado": "SP",
                                            "cep": "04538132",
                                            "descricao": "Consultoria especializada em arquitetura de microsserviços e soluções em nuvem."
                                        }
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "E-mail/CNPJ já cadastrado ou senha inválida",
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
                                            name = "CNPJ Duplicado",
                                            value = """{
                                                "codigo": 400,
                                                "mensagem": "Já existe uma empresa cadastrada com o CNPJ informado",
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
                                                "campo": "cnpj",
                                                "mensagem": "O CNPJ deve ter 14 dígitos"
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
    ResponseEntity<ApiResponseDTO<EmpresaResponseDTO>> cadastrarEmpresa(@Valid @RequestBody EmpresaRequestDTO requestDTO) {
        EmpresaResponseDTO responseDTO = cadastrarEmpresaUseCase.executar(requestDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.sucesso(responseDTO, "Empresa cadastrada com sucesso", HttpStatus.CREATED.value()))
    }

    @Operation(
            summary = "Lista empresas de forma anonimizada (Pré-Match)",
            description = "Retorna `EmpresaPublicoResponseDTO` contendo apenas ID, país, estado e descrição. Oculta nome, CNPJ, e-mail e CEP em respeito ao Princípio do Anonimato."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de empresas anônimas retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Lista Anonimizada de Empresas",
                                    value = """{
                                        "codigo": 200,
                                        "mensagem": "Operação realizada com sucesso",
                                        "tipo": "Sucesso",
                                        "dados": [
                                            {
                                                "id": 1,
                                                "pais": "Brasil",
                                                "estado": "SP",
                                                "descricao": "Consultoria especializada em arquitetura de microsserviços e soluções em nuvem."
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
    ResponseEntity<ApiResponseDTO<List<EmpresaPublicoResponseDTO>>> listarEmpresas() {
        List<EmpresaPublicoResponseDTO> lista = listarEmpresasUseCase.executar()
        return ResponseEntity.ok(ApiResponseDTO.sucesso(lista))
    }

    @Operation(
            summary = "Busca dados completos da empresa por ID (Restrito à Titular)",
            description = "Retorna `EmpresaResponseDTO` com todos os dados corporativos. Bloqueia o acesso de candidatos (`ROLE_CANDIDATO`) e de outras empresas com HTTP 403."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Dados da empresa retornados com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Empresa Completa (Titular)",
                                    value = """{
                                        "codigo": 200,
                                        "mensagem": "Operação realizada com sucesso",
                                        "tipo": "Sucesso",
                                        "dados": {
                                            "id": 1,
                                            "nome": "Tech Solutions Ltda",
                                            "cnpj": "12345678000199",
                                            "email": "rh@techsolutions.com.br",
                                            "pais": "Brasil",
                                            "estado": "SP",
                                            "cep": "04538132",
                                            "descricao": "Consultoria especializada em arquitetura de microsserviços e soluções em nuvem."
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
                    description = "Acesso negado: candidatos ou outras empresas não podem visualizar dados sensíveis",
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
                    description = "Empresa não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Empresa Não Encontrada",
                                    value = """{
                                        "codigo": 404,
                                        "mensagem": "Empresa não encontrada.",
                                        "tipo": "Recurso Não Encontrado"
                                    }"""
                            )
                    )
            )
    ])
    @GetMapping("/{id}")
    ResponseEntity<ApiResponseDTO<EmpresaResponseDTO>> buscarEmpresaPorId(
            @Parameter(description = "ID da empresa", example = "1") @PathVariable("id") Long id
    ) {
        EmpresaResponseDTO responseDTO = buscarEmpresaPorIdUseCase.executar(id)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(responseDTO))
    }

    @Operation(
            summary = "Atualiza os dados cadastrais da própria empresa",
            description = "Permite à empresa titular atualizar seus dados cadastrais e, opcionalmente, sua senha."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Empresa atualizada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Empresa Atualizada",
                                    value = """{
                                        "codigo": 200,
                                        "mensagem": "Empresa atualizada com sucesso",
                                        "tipo": "Sucesso",
                                        "dados": {
                                            "id": 1,
                                            "nome": "Tech Solutions Brasil Ltda",
                                            "cnpj": "12345678000199",
                                            "email": "rh@techsolutions.com.br",
                                            "pais": "Brasil",
                                            "estado": "SP",
                                            "cep": "04538132",
                                            "descricao": "Consultoria global especializada em arquitetura de microsserviços e soluções em nuvem."
                                        }
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Conflito de e-mail/CNPJ ou dados inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Conflito de E-mail ou CNPJ",
                                    value = """{
                                        "codigo": 400,
                                        "mensagem": "Já existe outra empresa cadastrada com o CNPJ informado",
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
                    description = "Acesso negado para alterar perfil de outra empresa",
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
                    description = "Empresa não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Empresa Não Encontrada",
                                    value = """{
                                        "codigo": 404,
                                        "mensagem": "Empresa não encontrada.",
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
    ResponseEntity<ApiResponseDTO<EmpresaResponseDTO>> atualizarEmpresa(
            @Parameter(description = "ID da empresa", example = "1") @PathVariable("id") Long id,
            @Valid @RequestBody EmpresaRequestDTO requestDTO
    ) {
        EmpresaResponseDTO responseDTO = atualizarEmpresaUseCase.executar(id, requestDTO)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(responseDTO, "Empresa atualizada com sucesso"))
    }

    @Operation(
            summary = "Deleta a conta da própria empresa",
            description = "Remove em cascata todas as curtidas associadas às vagas da empresa, suas vagas, seu perfil e credenciais de usuário."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Empresa deletada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Empresa Deletada",
                                    value = """{
                                        "codigo": 200,
                                        "mensagem": "Empresa deletada com sucesso",
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
                    description = "Acesso negado para deletar perfil de outra empresa",
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
                    description = "Empresa não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Empresa Não Encontrada",
                                    value = """{
                                        "codigo": 404,
                                        "mensagem": "Empresa não encontrada.",
                                        "tipo": "Recurso Não Encontrado"
                                    }"""
                            )
                    )
            )
    ])
    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponseDTO<Void>> deletarEmpresa(
            @Parameter(description = "ID da empresa", example = "1") @PathVariable("id") Long id
    ) {
        deletarEmpresaUseCase.executar(id)
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Empresa deletada com sucesso", HttpStatus.OK.value()))
    }

    @Operation(
            summary = "Lista os matches consolidados da empresa autenticada (Pós-Match)",
            description = "Retorna todos os matches ocorridos nas vagas da empresa autenticada (`candidatoCurtiuEm != null` e `empresaCurtiuEm != null`), incluindo os detalhes da vaga e revelando o nome completo e o e-mail de contato do candidato. Mantém CPF, CEP e data de nascimento ocultos."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de matches da empresa retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Matches da Empresa",
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
                                                "candidato": {
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
                                                },
                                                "candidatoNome": "João Silva",
                                                "candidatoEmail": "joao.silva@email.com",
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
                    description = "Acesso restrito a usuários com papel EMPRESA",
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
    @PreAuthorize("hasRole('EMPRESA')")
    @GetMapping("/matches")
    ResponseEntity<ApiResponseDTO<List<MatchEmpresaResponseDTO>>> listarMatches(
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Empresa empresa = empresaRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"))
        Long idEmpresa = empresa.getId()

        List<MatchEmpresaResponseDTO> matches = listarMatchesEmpresaUseCase.executar(idEmpresa)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(matches))
    }
}
