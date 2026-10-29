package org.ugustavodev.msnucleo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.dto.request.VagaRequestDTO
import org.ugustavodev.msnucleo.dto.response.CurtidaResponseDTO
import org.ugustavodev.msnucleo.dto.response.MatchVagaResponseDTO
import org.ugustavodev.msnucleo.dto.response.VagaResponseDTO
import org.ugustavodev.msnucleo.dto.response.VagaPublicoResponseDTO
import org.ugustavodev.msnucleo.dto.ApiResponseDTO
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.vagas.IAtualizarVagaUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.vagas.IBuscarVagaPorIdUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.vagas.ICadastrarVagaUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.curtidas.ICandidatoCurteVagaUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.curtidas.ICandidatoDescurteVagaUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.vagas.IDeletarVagaUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.curtidas.IEmpresaCurteCandidatoUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.curtidas.IEmpresaDescurteCandidatoUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.matches.IListarMatchesVagaUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.vagas.IListarVagasUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.vagas.IListarVagasSugeridasUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.matches.IListarCandidatosSugeridosUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.matches.IListarCandidatosInteressadosUseCase
import org.ugustavodev.msnucleo.dto.response.CandidatoPublicoResponseDTO

@RestController
@RequestMapping("/api/vaga")
@Tag(name = "6. Vagas, Curtidas e Matches", description = "CRUD de vagas, listagens de sugestões e interessados, registro de curtidas e consulta de matches por vaga")
class VagaController {

    private final ICadastrarVagaUseCase cadastrarVagaUseCase
    private final IBuscarVagaPorIdUseCase buscarVagaPorIdUseCase
    private final IListarVagasUseCase listarVagasUseCase
    private final IListarVagasSugeridasUseCase listarVagasSugeridasUseCase
    private final IListarCandidatosSugeridosUseCase listarCandidatosSugeridosUseCase
    private final IListarCandidatosInteressadosUseCase listarCandidatosInteressadosUseCase
    private final IAtualizarVagaUseCase atualizarVagaUseCase
    private final IDeletarVagaUseCase deletarVagaUseCase
    private final ICandidatoCurteVagaUseCase candidatoCurteVagaUseCase
    private final IEmpresaCurteCandidatoUseCase empresaCurteCandidatoUseCase
    private final ICandidatoDescurteVagaUseCase candidatoDescurteVagaUseCase
    private final IEmpresaDescurteCandidatoUseCase empresaDescurteCandidatoUseCase
    private final IListarMatchesVagaUseCase listarMatchesVagaUseCase
    private final CandidatoRepository candidatoRepository
    private final EmpresaRepository empresaRepository

    VagaController(
            ICadastrarVagaUseCase cadastrarVagaUseCase,
            IBuscarVagaPorIdUseCase buscarVagaPorIdUseCase,
            IListarVagasUseCase listarVagasUseCase,
            IListarVagasSugeridasUseCase listarVagasSugeridasUseCase,
            IListarCandidatosSugeridosUseCase listarCandidatosSugeridosUseCase,
            IListarCandidatosInteressadosUseCase listarCandidatosInteressadosUseCase,
            IAtualizarVagaUseCase atualizarVagaUseCase,
            IDeletarVagaUseCase deletarVagaUseCase,
            ICandidatoCurteVagaUseCase candidatoCurteVagaUseCase,
            IEmpresaCurteCandidatoUseCase empresaCurteCandidatoUseCase,
            ICandidatoDescurteVagaUseCase candidatoDescurteVagaUseCase,
            IEmpresaDescurteCandidatoUseCase empresaDescurteCandidatoUseCase,
            IListarMatchesVagaUseCase listarMatchesVagaUseCase,
            CandidatoRepository candidatoRepository,
            EmpresaRepository empresaRepository) {
        this.cadastrarVagaUseCase = cadastrarVagaUseCase
        this.buscarVagaPorIdUseCase = buscarVagaPorIdUseCase
        this.listarVagasUseCase = listarVagasUseCase
        this.listarVagasSugeridasUseCase = listarVagasSugeridasUseCase
        this.listarCandidatosSugeridosUseCase = listarCandidatosSugeridosUseCase
        this.listarCandidatosInteressadosUseCase = listarCandidatosInteressadosUseCase
        this.atualizarVagaUseCase = atualizarVagaUseCase
        this.deletarVagaUseCase = deletarVagaUseCase
        this.candidatoCurteVagaUseCase = candidatoCurteVagaUseCase
        this.empresaCurteCandidatoUseCase = empresaCurteCandidatoUseCase
        this.candidatoDescurteVagaUseCase = candidatoDescurteVagaUseCase
        this.empresaDescurteCandidatoUseCase = empresaDescurteCandidatoUseCase
        this.listarMatchesVagaUseCase = listarMatchesVagaUseCase
        this.candidatoRepository = candidatoRepository
        this.empresaRepository = empresaRepository
    }

    @Operation(
            summary = "Publica uma nova vaga",
            description = "Cria uma vaga vinculada à empresa autenticada (`ROLE_EMPRESA`)."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "201",
                    description = "Vaga cadastrada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Vaga Cadastrada",
                                    value = """{
                                        "codigo": 201,
                                        "mensagem": "Vaga cadastrada com sucesso",
                                        "tipo": "Sucesso",
                                        "dados": {
                                            "id": 5,
                                            "nome": "Pessoa Desenvolvedora Backend Groovy/Spring",
                                            "descricao": "Atuação no desenvolvimento e manutenção de microsserviços RESTful com Spring Boot e PostgreSQL.",
                                            "local": "São Paulo - SP (Remoto)",
                                            "nivel": "Pleno",
                                            "empresaId": 1,
                                            "empresaNome": "Tech Solutions Ltda"
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
                                            },
                                            {
                                                "campo": "descricao",
                                                "mensagem": "O campo descrição é obrigatório"
                                            }
                                        ]
                                    }"""
                            )
                    )
            )
    ])
    @PreAuthorize("hasRole('EMPRESA')")
    @PostMapping
    ResponseEntity<ApiResponseDTO<VagaResponseDTO>> cadastrarVaga(
            @Valid @RequestBody VagaRequestDTO requestDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Empresa empresa = empresaRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow { new RuntimeException("Empresa não encontrada") }
        
        VagaResponseDTO responseDTO = cadastrarVagaUseCase.executar(requestDTO, empresa.getId())
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.sucesso(responseDTO, "Vaga cadastrada com sucesso", HttpStatus.CREATED.value()))
    }

    @Operation(
            summary = "Lista vagas sem expor o nome da empresa",
            description = "Retorna `VagaPublicoResponseDTO` para todas as vagas ou filtradas opcionalmente por `empresaId`."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de vagas retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Lista Pública de Vagas",
                                    value = """{
                                        "codigo": 200,
                                        "mensagem": "Operação realizada com sucesso",
                                        "tipo": "Sucesso",
                                        "dados": [
                                            {
                                                "id": 5,
                                                "nome": "Pessoa Desenvolvedora Backend Groovy/Spring",
                                                "descricao": "Atuação no desenvolvimento e manutenção de microsserviços RESTful com Spring Boot e PostgreSQL.",
                                                "local": "São Paulo - SP (Remoto)",
                                                "nivel": "Pleno",
                                                "empresaId": 1
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
    ResponseEntity<ApiResponseDTO<List<VagaPublicoResponseDTO>>> listarVagas(
            @Parameter(description = "Filtra vagas pelo ID da empresa (opcional)", example = "1")
            @RequestParam(name = "empresaId", required = false) Long empresaId
    ) {
        List<VagaPublicoResponseDTO> lista = listarVagasUseCase.executar(empresaId)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(lista))
    }

    @Operation(
            summary = "Busca detalhes de uma vaga por ID (Anonimato Condicional)",
            description = "Retorna `VagaResponseDTO`. Os campos `empresaId` e `empresaNome` são preenchidos apenas para a empresa autora da vaga " +
                    "ou para candidatos que já possuam Match na vaga; caso contrário, retornam `null`."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Detalhes da vaga retornados com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = [
                                    @ExampleObject(
                                            name = "Visão Pré-Match (Candidato sem Match)",
                                            summary = "Oculta empresaId e empresaNome antes do Match",
                                            value = """{
                                                "codigo": 200,
                                                "mensagem": "Operação realizada com sucesso",
                                                "tipo": "Sucesso",
                                                "dados": {
                                                    "id": 5,
                                                    "nome": "Pessoa Desenvolvedora Backend Groovy/Spring",
                                                    "descricao": "Atuação no desenvolvimento e manutenção de microsserviços RESTful com Spring Boot e PostgreSQL.",
                                                    "local": "São Paulo - SP (Remoto)",
                                                    "nivel": "Pleno",
                                                    "empresaId": null,
                                                    "empresaNome": null
                                                }
                                            }"""
                                    ),
                                    @ExampleObject(
                                            name = "Visão Pós-Match ou Empresa Autora",
                                            summary = "Exibe empresaId e empresaNome após o Match ou para a empresa dona",
                                            value = """{
                                                "codigo": 200,
                                                "mensagem": "Operação realizada com sucesso",
                                                "tipo": "Sucesso",
                                                "dados": {
                                                    "id": 5,
                                                    "nome": "Pessoa Desenvolvedora Backend Groovy/Spring",
                                                    "descricao": "Atuação no desenvolvimento e manutenção de microsserviços RESTful com Spring Boot e PostgreSQL.",
                                                    "local": "São Paulo - SP (Remoto)",
                                                    "nivel": "Pleno",
                                                    "empresaId": 1,
                                                    "empresaNome": "Tech Solutions Ltda"
                                                }
                                            }"""
                                    )
                            ]
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
                    responseCode = "404",
                    description = "Vaga não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Vaga Não Encontrada",
                                    value = """{
                                        "codigo": 404,
                                        "mensagem": "Vaga não encontrada para o ID: 5",
                                        "tipo": "Recurso Não Encontrado"
                                    }"""
                            )
                    )
            )
    ])
    @GetMapping("/{id}")
    ResponseEntity<ApiResponseDTO<VagaResponseDTO>> buscarVagaPorId(
            @Parameter(description = "ID da vaga", example = "5") @PathVariable("id") Long id
    ) {
        VagaResponseDTO responseDTO = buscarVagaPorIdUseCase.executar(id)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(responseDTO))
    }

    @Operation(
            summary = "Atualiza uma vaga existente",
            description = "Permite à empresa autora atualizar os dados de uma vaga de sua propriedade."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Vaga atualizada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Vaga Atualizada",
                                    value = """{
                                        "codigo": 200,
                                        "mensagem": "Vaga atualizada com sucesso",
                                        "tipo": "Sucesso",
                                        "dados": {
                                            "id": 5,
                                            "nome": "Pessoa Desenvolvedora Backend Sênior Groovy/Spring",
                                            "descricao": "Atuação na liderança técnica e evolução de microsserviços RESTful com Spring Boot e PostgreSQL.",
                                            "local": "São Paulo - SP (Remoto)",
                                            "nivel": "Sênior",
                                            "empresaId": 1,
                                            "empresaNome": "Tech Solutions Ltda"
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
                    description = "Acesso negado: a vaga pertence a outra empresa ou o usuário não é EMPRESA",
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
                    description = "Vaga não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Vaga Não Encontrada",
                                    value = """{
                                        "codigo": 404,
                                        "mensagem": "Vaga não encontrada para o ID: 5",
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
    @PreAuthorize("hasRole('EMPRESA')")
    @PutMapping("/{id}")
    ResponseEntity<ApiResponseDTO<VagaResponseDTO>> atualizarVaga(
            @Parameter(description = "ID da vaga", example = "5") @PathVariable("id") Long id,
            @Valid @RequestBody VagaRequestDTO requestDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Empresa empresa = empresaRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow { new RuntimeException("Empresa não encontrada") }
                
        VagaResponseDTO responseDTO = atualizarVagaUseCase.executar(id, requestDTO, empresa.getId())
        return ResponseEntity.ok(ApiResponseDTO.sucesso(responseDTO, "Vaga atualizada com sucesso"))
    }

    @Operation(
            summary = "Exclui uma vaga",
            description = "Remove em cascata todas as curtidas vinculadas à vaga e exclui a vaga, validando se ela pertence à empresa autenticada."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Vaga deletada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Vaga Deletada",
                                    value = """{
                                        "codigo": 200,
                                        "mensagem": "Vaga deletada com sucesso",
                                        "tipo": "Sucesso"
                                    }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Violação de propriedade da vaga",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Vaga de Outra Empresa",
                                    value = """{
                                        "codigo": 400,
                                        "mensagem": "Você não tem permissão para deletar esta vaga.",
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
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Vaga não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Vaga Não Encontrada",
                                    value = """{
                                        "codigo": 404,
                                        "mensagem": "Vaga não encontrada para o ID: 5",
                                        "tipo": "Recurso Não Encontrado"
                                    }"""
                            )
                    )
            )
    ])
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPRESA')")
    ResponseEntity<ApiResponseDTO<Void>> deletarVaga(
            @Parameter(description = "ID da vaga", example = "5") @PathVariable("id") Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Empresa empresa = empresaRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow { new RuntimeException("Empresa não encontrada") }

        deletarVagaUseCase.executar(id, empresa.getId())
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Vaga deletada com sucesso", HttpStatus.OK.value()))
    }

    @Operation(
            summary = "Candidato curte uma vaga",
            description = "Registra o interesse do candidato autenticado na vaga. Caso a empresa já tenha curtido o candidato para esta vaga, consolida o Match (`match: true`)."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Vaga curtida com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = [
                                    @ExampleObject(
                                            name = "Curtida Unilateral (Sem Match ainda)",
                                            summary = "Candidato curtiu primeiro; aguarda curtida da empresa",
                                            value = """{
                                                "codigo": 200,
                                                "mensagem": "Vaga curtida com sucesso",
                                                "tipo": "Sucesso",
                                                "dados": {
                                                    "idCurtida": 10,
                                                    "candidatoId": 1,
                                                    "vagaId": 5,
                                                    "candidatoCurtiuEm": "2026-10-23T09:00:00",
                                                    "empresaCurtiuEm": null,
                                                    "match": false
                                                }
                                            }"""
                                    ),
                                    @ExampleObject(
                                            name = "Curtida Consolidando Match",
                                            summary = "Empresa já havia curtido o candidato; Match confirmado",
                                            value = """{
                                                "codigo": 200,
                                                "mensagem": "Vaga curtida com sucesso",
                                                "tipo": "Sucesso",
                                                "dados": {
                                                    "idCurtida": 10,
                                                    "candidatoId": 1,
                                                    "vagaId": 5,
                                                    "candidatoCurtiuEm": "2026-10-23T09:15:00",
                                                    "empresaCurtiuEm": "2026-10-23T09:00:00",
                                                    "match": true
                                                }
                                            }"""
                                    )
                            ]
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
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Vaga ou candidato não encontrados",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Vaga Não Encontrada",
                                    value = """{
                                        "codigo": 404,
                                        "mensagem": "Vaga não encontrada para o ID: 5",
                                        "tipo": "Recurso Não Encontrado"
                                    }"""
                            )
                    )
            )
    ])
    @PreAuthorize("hasRole('CANDIDATO')")
    @PostMapping("/{idVaga}/curtir")
    ResponseEntity<ApiResponseDTO<CurtidaResponseDTO>> curtirVaga(
            @Parameter(description = "ID da vaga a ser curtida", example = "5") @PathVariable("idVaga") Long idVaga,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Candidato candidato = candidatoRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow { new RuntimeException("Candidato não encontrado") }
        Long idCandidato = candidato.getId()

        CurtidaResponseDTO curtidaResponse = candidatoCurteVagaUseCase.executar(idCandidato, idVaga)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(curtidaResponse, "Vaga curtida com sucesso"))
    }

    @Operation(
            summary = "Candidato remove curtida de uma vaga",
            description = "Remove o interesse do candidato na vaga. Se havia Match, desfaz o Match mantendo apenas a curtida unilateral da empresa; se não havia curtida da empresa, exclui o registro."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Curtida removida com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Curtida Removida",
                                    value = """{
                                        "codigo": 200,
                                        "mensagem": "Curtida removida com sucesso",
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
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Curtida não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Curtida Não Encontrada",
                                    value = """{
                                        "codigo": 404,
                                        "mensagem": "Curtida não encontrada.",
                                        "tipo": "Recurso Não Encontrado"
                                    }"""
                            )
                    )
            )
    ])
    @PreAuthorize("hasRole('CANDIDATO')")
    @DeleteMapping("/{idVaga}/curtir")
    ResponseEntity<ApiResponseDTO<Void>> descurtirVaga(
            @Parameter(description = "ID da vaga", example = "5") @PathVariable("idVaga") Long idVaga,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Candidato candidato = candidatoRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow { new RuntimeException("Candidato não encontrado") }
        Long idCandidato = candidato.getId()

        candidatoDescurteVagaUseCase.executar(idCandidato, idVaga)
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Curtida removida com sucesso", HttpStatus.OK.value()))
    }

    @Operation(
            summary = "Empresa curte um candidato para uma vaga",
            description = "Registra o interesse da empresa autenticada em um candidato para uma vaga específica de sua propriedade. " +
                    "Caso o candidato já tenha curtido a vaga, consolida o Match (`match: true`)."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Candidato curtido com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = [
                                    @ExampleObject(
                                            name = "Curtida Consolidando Match",
                                            summary = "Candidato já havia curtido a vaga; Match confirmado",
                                            value = """{
                                                "codigo": 200,
                                                "mensagem": "Candidato curtido com sucesso",
                                                "tipo": "Sucesso",
                                                "dados": {
                                                    "idCurtida": 10,
                                                    "candidatoId": 1,
                                                    "vagaId": 5,
                                                    "candidatoCurtiuEm": "2026-10-23T09:00:00",
                                                    "empresaCurtiuEm": "2026-10-23T09:15:00",
                                                    "match": true
                                                }
                                            }"""
                                    ),
                                    @ExampleObject(
                                            name = "Curtida Unilateral da Empresa",
                                            summary = "Empresa curtiu candidato sugerido; aguarda curtida do candidato",
                                            value = """{
                                                "codigo": 200,
                                                "mensagem": "Candidato curtido com sucesso",
                                                "tipo": "Sucesso",
                                                "dados": {
                                                    "idCurtida": 10,
                                                    "candidatoId": 1,
                                                    "vagaId": 5,
                                                    "candidatoCurtiuEm": null,
                                                    "empresaCurtiuEm": "2026-10-23T09:00:00",
                                                    "match": false
                                                }
                                            }"""
                                    )
                            ]
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
                    description = "Acesso negado: a vaga não pertence à empresa autenticada",
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
                    description = "Candidato ou vaga não encontrados",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Candidato ou Vaga Não Encontrados",
                                    value = """{
                                        "codigo": 404,
                                        "mensagem": "Candidato não encontrado para o ID: 1",
                                        "tipo": "Recurso Não Encontrado"
                                    }"""
                            )
                    )
            )
    ])
    @PreAuthorize("hasRole('EMPRESA')")
    @PostMapping("/{idVaga}/candidato/{idCandidato}/curtir")
    ResponseEntity<ApiResponseDTO<CurtidaResponseDTO>> curtirCandidato(
            @Parameter(description = "ID da vaga pertencente à empresa", example = "5") @PathVariable("idVaga") Long idVaga,
            @Parameter(description = "ID do candidato a ser curtido", example = "1") @PathVariable("idCandidato") Long idCandidato,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Empresa empresa = empresaRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow { new RuntimeException("Empresa não encontrada") }
        Long idEmpresa = empresa.getId()

        CurtidaResponseDTO curtidaResponse = empresaCurteCandidatoUseCase.executar(idEmpresa, idCandidato, idVaga)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(curtidaResponse, "Candidato curtido com sucesso"))
    }

    @Operation(
            summary = "Empresa remove curtida em um candidato para uma vaga",
            description = "Remove o interesse da empresa no candidato para a vaga informada. Se havia Match, desfaz o Match mantendo a curtida do candidato; caso contrário, remove o registro."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Curtida removida com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Curtida Removida",
                                    value = """{
                                        "codigo": 200,
                                        "mensagem": "Curtida removida com sucesso",
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
                    description = "Acesso negado: a vaga não pertence à empresa autenticada",
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
                    description = "Vaga ou curtida não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Curtida Não Encontrada",
                                    value = """{
                                        "codigo": 404,
                                        "mensagem": "Curtida não encontrada.",
                                        "tipo": "Recurso Não Encontrado"
                                    }"""
                            )
                    )
            )
    ])
    @PreAuthorize("hasRole('EMPRESA')")
    @DeleteMapping("/{idVaga}/candidato/{idCandidato}/curtir")
    ResponseEntity<ApiResponseDTO<Void>> descurtirCandidato(
            @Parameter(description = "ID da vaga pertencente à empresa", example = "5") @PathVariable("idVaga") Long idVaga,
            @Parameter(description = "ID do candidato", example = "1") @PathVariable("idCandidato") Long idCandidato,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Empresa empresa = empresaRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow { new RuntimeException("Empresa não encontrada") }
        Long idEmpresa = empresa.getId()

        empresaDescurteCandidatoUseCase.executar(idEmpresa, idCandidato, idVaga)
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Curtida removida com sucesso", HttpStatus.OK.value()))
    }

    @Operation(
            summary = "Lista os matches consolidados de uma vaga (Pós-Match)",
            description = "Retorna `MatchVagaResponseDTO` para todos os candidatos com interesse mútuo na vaga, revelando `candidatoNome` " +
                    "e `candidatoEmail` para a empresa autora (mantendo CPF, CEP e data de nascimento ocultos)."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de matches da vaga retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Matches da Vaga",
                                    value = """{
                                        "codigo": 200,
                                        "mensagem": "Operação realizada com sucesso",
                                        "tipo": "Sucesso",
                                        "dados": [
                                            {
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
                    description = "Acesso negado: a vaga não pertence à empresa autenticada",
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
                    description = "Vaga não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Vaga Não Encontrada",
                                    value = """{
                                        "codigo": 404,
                                        "mensagem": "Vaga não encontrada.",
                                        "tipo": "Recurso Não Encontrado"
                                    }"""
                            )
                    )
            )
    ])
    @PreAuthorize("hasRole('EMPRESA')")
    @GetMapping("/{idVaga}/matches")
    ResponseEntity<ApiResponseDTO<List<MatchVagaResponseDTO>>> listarMatches(
            @Parameter(description = "ID da vaga pertencente à empresa", example = "5") @PathVariable("idVaga") Long idVaga,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Empresa empresa = empresaRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow { new RuntimeException("Empresa não encontrada") }
        Long idEmpresa = empresa.getId()
        
        List<MatchVagaResponseDTO> matches = listarMatchesVagaUseCase.executar(idVaga, idEmpresa)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(matches))
    }

    @Operation(
            summary = "Lista vagas sugeridas para o candidato autenticado (Pré-Match)",
            description = "Retorna todas as vagas nas quais o candidato autenticado ainda não registrou curtida (`VagaPublicoResponseDTO`, sem expor o nome da empresa)."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de vagas sugeridas retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Vagas Sugeridas para o Candidato",
                                    value = """{
                                        "codigo": 200,
                                        "mensagem": "Operação realizada com sucesso",
                                        "tipo": "Sucesso",
                                        "dados": [
                                            {
                                                "id": 5,
                                                "nome": "Pessoa Desenvolvedora Backend Groovy/Spring",
                                                "descricao": "Atuação no desenvolvimento e manutenção de microsserviços RESTful com Spring Boot e PostgreSQL.",
                                                "local": "São Paulo - SP (Remoto)",
                                                "nivel": "Pleno",
                                                "empresaId": 1
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
    @GetMapping("/sugeridas")
    ResponseEntity<ApiResponseDTO<List<VagaPublicoResponseDTO>>> listarVagasSugeridas(
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Candidato candidato = candidatoRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow { new RuntimeException("Candidato não encontrado") }
        
        List<VagaPublicoResponseDTO> vagas = listarVagasSugeridasUseCase.executar(candidato.getId())
        return ResponseEntity.ok(ApiResponseDTO.sucesso(vagas))
    }

    @Operation(
            summary = "Lista candidatos anônimos sugeridos para uma vaga (Pré-Match)",
            description = "Retorna os candidatos (`CandidatoPublicoResponseDTO`) nos quais a empresa ainda não registrou curtida para a vaga informada."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de candidatos sugeridos retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Candidatos Sugeridos (Anônimos)",
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
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado: a vaga não pertence à empresa autenticada",
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
                    description = "Vaga não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Vaga Não Encontrada",
                                    value = """{
                                        "codigo": 404,
                                        "mensagem": "Vaga não encontrada para o ID: 5",
                                        "tipo": "Recurso Não Encontrado"
                                    }"""
                            )
                    )
            )
    ])
    @PreAuthorize("hasRole('EMPRESA')")
    @GetMapping("/{idVaga}/candidatos/sugeridos")
    ResponseEntity<ApiResponseDTO<List<CandidatoPublicoResponseDTO>>> listarCandidatosSugeridos(
            @Parameter(description = "ID da vaga pertencente à empresa", example = "5") @PathVariable("idVaga") Long idVaga,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Empresa empresa = empresaRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow { new RuntimeException("Empresa não encontrada") }
        
        List<CandidatoPublicoResponseDTO> candidatos = listarCandidatosSugeridosUseCase.executar(idVaga, empresa.getId())
        return ResponseEntity.ok(ApiResponseDTO.sucesso(candidatos))
    }

    @Operation(
            summary = "Lista candidatos anônimos interessados em uma vaga (Pré-Match)",
            description = "Retorna os candidatos (`CandidatoPublicoResponseDTO`) que já curtiram a vaga, mas ainda não foram curtidos pela empresa."
    )
    @ApiResponses([
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de candidatos interessados retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Candidatos Interessados na Vaga (Anônimos)",
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
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso negado: a vaga não pertence à empresa autenticada",
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
                    description = "Vaga não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Vaga Não Encontrada",
                                    value = """{
                                        "codigo": 404,
                                        "mensagem": "Vaga não encontrada para o ID: 5",
                                        "tipo": "Recurso Não Encontrado"
                                    }"""
                            )
                    )
            )
    ])
    @PreAuthorize("hasRole('EMPRESA')")
    @GetMapping("/{idVaga}/candidatos/interessados")
    ResponseEntity<ApiResponseDTO<List<CandidatoPublicoResponseDTO>>> listarCandidatosInteressados(
            @Parameter(description = "ID da vaga pertencente à empresa", example = "5") @PathVariable("idVaga") Long idVaga,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Empresa empresa = empresaRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow { new RuntimeException("Empresa não encontrada") }
        
        List<CandidatoPublicoResponseDTO> candidatos = listarCandidatosInteressadosUseCase.executar(idVaga, empresa.getId())
        return ResponseEntity.ok(ApiResponseDTO.sucesso(candidatos))
    }

}
