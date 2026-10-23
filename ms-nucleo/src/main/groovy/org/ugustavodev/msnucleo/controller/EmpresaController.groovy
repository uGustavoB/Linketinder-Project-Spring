package org.ugustavodev.msnucleo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.ugustavodev.msnucleo.dto.request.EmpresaRequestDTO
import org.ugustavodev.msnucleo.dto.response.EmpresaResponseDTO
import org.ugustavodev.msnucleo.dto.response.EmpresaPublicoResponseDTO
import org.ugustavodev.msnucleo.dto.ApiResponseDTO
import org.ugustavodev.msnucleo.usecase.interfaces.IAtualizarEmpresaUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IBuscarEmpresaPorIdUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.ICadastrarEmpresaUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IDeletarEmpresaUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IListarEmpresasUseCase

@RestController
@RequestMapping("/api/empresa")
@Tag(name = "5. Empresas", description = "Gestão cadastral de empresas e listagem pública anonimizada")
class EmpresaController {
    private final ICadastrarEmpresaUseCase cadastrarEmpresaUseCase
    private final IBuscarEmpresaPorIdUseCase buscarEmpresaPorIdUseCase
    private final IListarEmpresasUseCase listarEmpresasUseCase
    private final IAtualizarEmpresaUseCase atualizarEmpresaUseCase
    private final IDeletarEmpresaUseCase deletarEmpresaUseCase

    EmpresaController(
            ICadastrarEmpresaUseCase cadastrarEmpresaUseCase,
            IBuscarEmpresaPorIdUseCase buscarEmpresaPorIdUseCase,
            IListarEmpresasUseCase listarEmpresasUseCase,
            IAtualizarEmpresaUseCase atualizarEmpresaUseCase,
            IDeletarEmpresaUseCase deletarEmpresaUseCase
    ) {
        this.cadastrarEmpresaUseCase = cadastrarEmpresaUseCase
        this.buscarEmpresaPorIdUseCase = buscarEmpresaPorIdUseCase
        this.listarEmpresasUseCase = listarEmpresasUseCase
        this.atualizarEmpresaUseCase = atualizarEmpresaUseCase
        this.deletarEmpresaUseCase = deletarEmpresaUseCase
    }

    @Operation(
            summary = "Cadastra uma nova empresa (Público)",
            description = "Cria uma nova conta corporativa com perfil `ROLE_EMPRESA`. O e-mail e o CNPJ devem ser únicos no sistema."
    )
    @SecurityRequirements
    @ApiResponses([
            @ApiResponse(responseCode = "201", description = "Empresa cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "E-mail/CNPJ já cadastrado ou senha inválida"),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos informados")
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
            @ApiResponse(responseCode = "200", description = "Lista de empresas anônimas retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
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
            @ApiResponse(responseCode = "200", description = "Dados da empresa retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado: candidatos ou outras empresas não podem visualizar dados sensíveis"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada")
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
            @ApiResponse(responseCode = "200", description = "Empresa atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Conflito de e-mail/CNPJ ou dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado para alterar perfil de outra empresa"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada"),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos informados")
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
            @ApiResponse(responseCode = "200", description = "Empresa deletada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado para deletar perfil de outra empresa"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada")
    ])
    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponseDTO<Void>> deletarEmpresa(
            @Parameter(description = "ID da empresa", example = "1") @PathVariable("id") Long id
    ) {
        deletarEmpresaUseCase.executar(id)
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Empresa deletada com sucesso", HttpStatus.OK.value()))
    }
}
