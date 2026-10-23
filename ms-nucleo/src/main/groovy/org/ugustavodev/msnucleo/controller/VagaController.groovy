package org.ugustavodev.msnucleo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
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
import org.ugustavodev.msnucleo.usecase.interfaces.IAtualizarVagaUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IBuscarVagaPorIdUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.ICadastrarVagaUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.ICandidatoCurteVagaUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.ICandidatoDescurteVagaUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IDeletarVagaUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IEmpresaCurteCandidatoUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IEmpresaDescurteCandidatoUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IListarMatchesVagaUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IListarVagasUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IListarVagasSugeridasUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IListarCandidatosSugeridosUseCase
import org.ugustavodev.msnucleo.usecase.interfaces.IListarCandidatosInteressadosUseCase
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
            EmpresaRepository empresaRepository
    ) {
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
            @ApiResponse(responseCode = "201", description = "Vaga cadastrada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso restrito a usuários com papel EMPRESA"),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos informados")
    ])
    @PreAuthorize("hasRole('EMPRESA')")
    @PostMapping
    ResponseEntity<ApiResponseDTO<VagaResponseDTO>> cadastrarVaga(
            @Valid @RequestBody VagaRequestDTO requestDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Empresa empresa = empresaRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"))
        
        VagaResponseDTO responseDTO = cadastrarVagaUseCase.executar(requestDTO, empresa.getId())
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.sucesso(responseDTO, "Vaga cadastrada com sucesso", HttpStatus.CREATED.value()))
    }

    @Operation(
            summary = "Lista vagas sem expor o nome da empresa",
            description = "Retorna `VagaPublicoResponseDTO` para todas as vagas ou filtradas opcionalmente por `empresaId`."
    )
    @ApiResponses([
            @ApiResponse(responseCode = "200", description = "Lista de vagas retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
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
            description = "Retorna `VagaResponseDTO`. Os campos `empresaId` e `empresaNome` são preenchidos apenas para a empresa autora da vaga ou para candidatos que já possuam Match na vaga; caso contrário, retornam `null`."
    )
    @ApiResponses([
            @ApiResponse(responseCode = "200", description = "Detalhes da vaga retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Vaga não encontrada")
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
            @ApiResponse(responseCode = "200", description = "Vaga atualizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado: a vaga pertence a outra empresa ou o usuário não é EMPRESA"),
            @ApiResponse(responseCode = "404", description = "Vaga não encontrada"),
            @ApiResponse(responseCode = "422", description = "Erro de validação nos campos informados")
    ])
    @PreAuthorize("hasRole('EMPRESA')")
    @PutMapping("/{id}")
    ResponseEntity<ApiResponseDTO<VagaResponseDTO>> atualizarVaga(
            @Parameter(description = "ID da vaga", example = "5") @PathVariable("id") Long id,
            @Valid @RequestBody VagaRequestDTO requestDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Empresa empresa = empresaRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"))
                
        VagaResponseDTO responseDTO = atualizarVagaUseCase.executar(id, requestDTO, empresa.getId())
        return ResponseEntity.ok(ApiResponseDTO.sucesso(responseDTO, "Vaga atualizada com sucesso"))
    }

    @Operation(
            summary = "Exclui uma vaga",
            description = "Remove em cascata todas as curtidas vinculadas à vaga e exclui a vaga, validando se ela pertence à empresa autenticada."
    )
    @ApiResponses([
            @ApiResponse(responseCode = "200", description = "Vaga deletada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Violação de propriedade da vaga"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso restrito a usuários com papel EMPRESA"),
            @ApiResponse(responseCode = "404", description = "Vaga não encontrada")
    ])
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPRESA')")
    ResponseEntity<ApiResponseDTO<Void>> deletarVaga(
            @Parameter(description = "ID da vaga", example = "5") @PathVariable("id") Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Empresa empresa = empresaRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"))

        deletarVagaUseCase.executar(id, empresa.getId())
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Vaga deletada com sucesso", HttpStatus.OK.value()))
    }

    @Operation(
            summary = "Candidato curte uma vaga",
            description = "Registra o interesse do candidato autenticado na vaga. Caso a empresa já tenha curtido o candidato para esta vaga, consolida o Match (`match: true`)."
    )
    @ApiResponses([
            @ApiResponse(responseCode = "200", description = "Vaga curtida com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso restrito a usuários com papel CANDIDATO"),
            @ApiResponse(responseCode = "404", description = "Vaga ou candidato não encontrados")
    ])
    @PreAuthorize("hasRole('CANDIDATO')")
    @PostMapping("/{idVaga}/curtir")
    ResponseEntity<ApiResponseDTO<CurtidaResponseDTO>> curtirVaga(
            @Parameter(description = "ID da vaga a ser curtida", example = "5") @PathVariable("idVaga") Long idVaga,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Candidato candidato = candidatoRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Candidato não encontrado"))
        Long idCandidato = candidato.getId()

        CurtidaResponseDTO curtidaResponse = candidatoCurteVagaUseCase.executar(idCandidato, idVaga)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(curtidaResponse, "Vaga curtida com sucesso"))
    }

    @Operation(
            summary = "Candidato remove curtida de uma vaga",
            description = "Remove o interesse do candidato na vaga. Se havia Match, desfaz o Match mantendo apenas a curtida unilateral da empresa; se não havia curtida da empresa, exclui o registro."
    )
    @ApiResponses([
            @ApiResponse(responseCode = "200", description = "Curtida removida com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso restrito a usuários com papel CANDIDATO"),
            @ApiResponse(responseCode = "404", description = "Curtida não encontrada")
    ])
    @PreAuthorize("hasRole('CANDIDATO')")
    @DeleteMapping("/{idVaga}/curtir")
    ResponseEntity<ApiResponseDTO<Void>> descurtirVaga(
            @Parameter(description = "ID da vaga", example = "5") @PathVariable("idVaga") Long idVaga,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Candidato candidato = candidatoRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Candidato não encontrado"))
        Long idCandidato = candidato.getId()

        candidatoDescurteVagaUseCase.executar(idCandidato, idVaga)
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Curtida removida com sucesso", HttpStatus.OK.value()))
    }

    @Operation(
            summary = "Empresa curte um candidato para uma vaga",
            description = "Registra o interesse da empresa autenticada em um candidato para uma vaga específica de sua propriedade. Caso o candidato já tenha curtido a vaga, consolida o Match (`match: true`)."
    )
    @ApiResponses([
            @ApiResponse(responseCode = "200", description = "Candidato curtido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado: a vaga não pertence à empresa autenticada"),
            @ApiResponse(responseCode = "404", description = "Candidato ou vaga não encontrados")
    ])
    @PreAuthorize("hasRole('EMPRESA')")
    @PostMapping("/{idVaga}/candidato/{idCandidato}/curtir")
    ResponseEntity<ApiResponseDTO<CurtidaResponseDTO>> curtirCandidato(
            @Parameter(description = "ID da vaga pertencente à empresa", example = "5") @PathVariable("idVaga") Long idVaga,
            @Parameter(description = "ID do candidato a ser curtido", example = "1") @PathVariable("idCandidato") Long idCandidato,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Empresa empresa = empresaRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"))
        Long idEmpresa = empresa.getId()

        CurtidaResponseDTO curtidaResponse = empresaCurteCandidatoUseCase.executar(idEmpresa, idCandidato, idVaga)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(curtidaResponse, "Candidato curtido com sucesso"))
    }

    @Operation(
            summary = "Empresa remove curtida em um candidato para uma vaga",
            description = "Remove o interesse da empresa no candidato para a vaga informada. Se havia Match, desfaz o Match mantendo a curtida do candidato; caso contrário, remove o registro."
    )
    @ApiResponses([
            @ApiResponse(responseCode = "200", description = "Curtida removida com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado: a vaga não pertence à empresa autenticada"),
            @ApiResponse(responseCode = "404", description = "Vaga ou curtida não encontrada")
    ])
    @PreAuthorize("hasRole('EMPRESA')")
    @DeleteMapping("/{idVaga}/candidato/{idCandidato}/curtir")
    ResponseEntity<ApiResponseDTO<Void>> descurtirCandidato(
            @Parameter(description = "ID da vaga pertencente à empresa", example = "5") @PathVariable("idVaga") Long idVaga,
            @Parameter(description = "ID do candidato", example = "1") @PathVariable("idCandidato") Long idCandidato,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Empresa empresa = empresaRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"))
        Long idEmpresa = empresa.getId()

        empresaDescurteCandidatoUseCase.executar(idEmpresa, idCandidato, idVaga)
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Curtida removida com sucesso", HttpStatus.OK.value()))
    }

    @Operation(
            summary = "Lista os matches consolidados de uma vaga (Pós-Match)",
            description = "Retorna `MatchVagaResponseDTO` para todos os candidatos com interesse mútuo na vaga, revelando `candidatoNome` e `candidatoEmail` para a empresa autora (mantendo CPF, CEP e data de nascimento ocultos)."
    )
    @ApiResponses([
            @ApiResponse(responseCode = "200", description = "Lista de matches da vaga retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado: a vaga não pertence à empresa autenticada"),
            @ApiResponse(responseCode = "404", description = "Vaga não encontrada")
    ])
    @PreAuthorize("hasRole('EMPRESA')")
    @GetMapping("/{idVaga}/matches")
    ResponseEntity<ApiResponseDTO<List<MatchVagaResponseDTO>>> listarMatches(
            @Parameter(description = "ID da vaga pertencente à empresa", example = "5") @PathVariable("idVaga") Long idVaga,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Empresa empresa = empresaRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"))
        Long idEmpresa = empresa.getId()
        
        List<MatchVagaResponseDTO> matches = listarMatchesVagaUseCase.executar(idVaga, idEmpresa)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(matches))
    }

    @Operation(
            summary = "Lista vagas sugeridas para o candidato autenticado (Pré-Match)",
            description = "Retorna todas as vagas nas quais o candidato autenticado ainda não registrou curtida (`VagaPublicoResponseDTO`, sem expor o nome da empresa)."
    )
    @ApiResponses([
            @ApiResponse(responseCode = "200", description = "Lista de vagas sugeridas retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso restrito a usuários com papel CANDIDATO")
    ])
    @PreAuthorize("hasRole('CANDIDATO')")
    @GetMapping("/sugeridas")
    ResponseEntity<ApiResponseDTO<List<VagaPublicoResponseDTO>>> listarVagasSugeridas(
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Candidato candidato = candidatoRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Candidato não encontrado"))
        
        List<VagaPublicoResponseDTO> vagas = listarVagasSugeridasUseCase.executar(candidato.getId())
        return ResponseEntity.ok(ApiResponseDTO.sucesso(vagas))
    }

    @Operation(
            summary = "Lista candidatos anônimos sugeridos para uma vaga (Pré-Match)",
            description = "Retorna os candidatos (`CandidatoPublicoResponseDTO`) nos quais a empresa ainda não registrou curtida para a vaga informada."
    )
    @ApiResponses([
            @ApiResponse(responseCode = "200", description = "Lista de candidatos sugeridos retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado: a vaga não pertence à empresa autenticada"),
            @ApiResponse(responseCode = "404", description = "Vaga não encontrada")
    ])
    @PreAuthorize("hasRole('EMPRESA')")
    @GetMapping("/{idVaga}/candidatos/sugeridos")
    ResponseEntity<ApiResponseDTO<List<CandidatoPublicoResponseDTO>>> listarCandidatosSugeridos(
            @Parameter(description = "ID da vaga pertencente à empresa", example = "5") @PathVariable("idVaga") Long idVaga,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Empresa empresa = empresaRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"))
        
        List<CandidatoPublicoResponseDTO> candidatos = listarCandidatosSugeridosUseCase.executar(idVaga, empresa.getId())
        return ResponseEntity.ok(ApiResponseDTO.sucesso(candidatos))
    }

    @Operation(
            summary = "Lista candidatos anônimos interessados em uma vaga (Pré-Match)",
            description = "Retorna os candidatos (`CandidatoPublicoResponseDTO`) que já curtiram a vaga, mas ainda não foram curtidos pela empresa."
    )
    @ApiResponses([
            @ApiResponse(responseCode = "200", description = "Lista de candidatos interessados retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado: a vaga não pertence à empresa autenticada"),
            @ApiResponse(responseCode = "404", description = "Vaga não encontrada")
    ])
    @PreAuthorize("hasRole('EMPRESA')")
    @GetMapping("/{idVaga}/candidatos/interessados")
    ResponseEntity<ApiResponseDTO<List<CandidatoPublicoResponseDTO>>> listarCandidatosInteressados(
            @Parameter(description = "ID da vaga pertencente à empresa", example = "5") @PathVariable("idVaga") Long idVaga,
            @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Empresa empresa = empresaRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"))
        
        List<CandidatoPublicoResponseDTO> candidatos = listarCandidatosInteressadosUseCase.executar(idVaga, empresa.getId())
        return ResponseEntity.ok(ApiResponseDTO.sucesso(candidatos))
    }
}
