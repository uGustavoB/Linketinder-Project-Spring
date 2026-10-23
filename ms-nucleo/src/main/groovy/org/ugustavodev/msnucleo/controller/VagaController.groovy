package org.ugustavodev.msnucleo.controller

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

    @PreAuthorize("hasRole('EMPRESA')")
    @PostMapping
    ResponseEntity<ApiResponseDTO<VagaResponseDTO>> cadastrarVaga(
            @Valid @RequestBody VagaRequestDTO requestDTO,
            @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Empresa empresa = empresaRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"))
        
        VagaResponseDTO responseDTO = cadastrarVagaUseCase.executar(requestDTO, empresa.getId())
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.sucesso(responseDTO, "Vaga cadastrada com sucesso", HttpStatus.CREATED.value()))
    }

    @GetMapping
    ResponseEntity<ApiResponseDTO<List<VagaPublicoResponseDTO>>> listarVagas(@RequestParam(name = "empresaId", required = false) Long empresaId) {
        List<VagaPublicoResponseDTO> lista = listarVagasUseCase.executar(empresaId)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(lista))
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponseDTO<VagaResponseDTO>> buscarVagaPorId(@PathVariable("id") Long id) {
        VagaResponseDTO responseDTO = buscarVagaPorIdUseCase.executar(id)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(responseDTO))
    }

    @PreAuthorize("hasRole('EMPRESA')")
    @PutMapping("/{id}")
    ResponseEntity<ApiResponseDTO<VagaResponseDTO>> atualizarVaga(
            @PathVariable("id") Long id,
            @Valid @RequestBody VagaRequestDTO requestDTO,
            @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Empresa empresa = empresaRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"))
                
        VagaResponseDTO responseDTO = atualizarVagaUseCase.executar(id, requestDTO, empresa.getId())
        return ResponseEntity.ok(ApiResponseDTO.sucesso(responseDTO, "Vaga atualizada com sucesso"))
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPRESA')")
    ResponseEntity<ApiResponseDTO<Void>> deletarVaga(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Empresa empresa = empresaRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"))

        deletarVagaUseCase.executar(id, empresa.getId())
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Vaga deletada com sucesso", HttpStatus.OK.value()))
    }

    @PreAuthorize("hasRole('CANDIDATO')")
    @PostMapping("/{idVaga}/curtir")
    ResponseEntity<ApiResponseDTO<CurtidaResponseDTO>> curtirVaga(
            @PathVariable("idVaga") Long idVaga,
            @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Candidato candidato = candidatoRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Candidato não encontrado"))
        Long idCandidato = candidato.getId()

        CurtidaResponseDTO curtidaResponse = candidatoCurteVagaUseCase.executar(idCandidato, idVaga)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(curtidaResponse, "Vaga curtida com sucesso"))
    }

    @PreAuthorize("hasRole('CANDIDATO')")
    @DeleteMapping("/{idVaga}/curtir")
    ResponseEntity<ApiResponseDTO<Void>> descurtirVaga(
            @PathVariable("idVaga") Long idVaga,
            @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Candidato candidato = candidatoRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Candidato não encontrado"))
        Long idCandidato = candidato.getId()

        candidatoDescurteVagaUseCase.executar(idCandidato, idVaga)
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Curtida removida com sucesso", HttpStatus.OK.value()))
    }

    @PreAuthorize("hasRole('EMPRESA')")
    @PostMapping("/{idVaga}/candidato/{idCandidato}/curtir")
    ResponseEntity<ApiResponseDTO<CurtidaResponseDTO>> curtirCandidato(
            @PathVariable("idVaga") Long idVaga,
            @PathVariable("idCandidato") Long idCandidato,
            @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Empresa empresa = empresaRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"))
        Long idEmpresa = empresa.getId()

        CurtidaResponseDTO curtidaResponse = empresaCurteCandidatoUseCase.executar(idEmpresa, idCandidato, idVaga)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(curtidaResponse, "Candidato curtido com sucesso"))
    }

    @PreAuthorize("hasRole('EMPRESA')")
    @DeleteMapping("/{idVaga}/candidato/{idCandidato}/curtir")
    ResponseEntity<ApiResponseDTO<Void>> descurtirCandidato(
            @PathVariable("idVaga") Long idVaga,
            @PathVariable("idCandidato") Long idCandidato,
            @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Empresa empresa = empresaRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"))
        Long idEmpresa = empresa.getId()

        empresaDescurteCandidatoUseCase.executar(idEmpresa, idCandidato, idVaga)
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Curtida removida com sucesso", HttpStatus.OK.value()))
    }

    @PreAuthorize("hasRole('EMPRESA')")
    @GetMapping("/{idVaga}/matches")
    ResponseEntity<ApiResponseDTO<List<MatchVagaResponseDTO>>> listarMatches(
            @PathVariable("idVaga") Long idVaga,
            @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Empresa empresa = empresaRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"))
        Long idEmpresa = empresa.getId()
        
        List<MatchVagaResponseDTO> matches = listarMatchesVagaUseCase.executar(idVaga, idEmpresa)
        return ResponseEntity.ok(ApiResponseDTO.sucesso(matches))
    }

    @PreAuthorize("hasRole('CANDIDATO')")
    @GetMapping("/sugeridas")
    ResponseEntity<ApiResponseDTO<List<VagaPublicoResponseDTO>>> listarVagasSugeridas(@AuthenticationPrincipal Usuario usuarioAutenticado) {
        Candidato candidato = candidatoRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Candidato não encontrado"))
        
        List<VagaPublicoResponseDTO> vagas = listarVagasSugeridasUseCase.executar(candidato.getId())
        return ResponseEntity.ok(ApiResponseDTO.sucesso(vagas))
    }

    @PreAuthorize("hasRole('EMPRESA')")
    @GetMapping("/{idVaga}/candidatos/sugeridos")
    ResponseEntity<ApiResponseDTO<List<CandidatoPublicoResponseDTO>>> listarCandidatosSugeridos(
            @PathVariable("idVaga") Long idVaga,
            @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Empresa empresa = empresaRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"))
        
        List<CandidatoPublicoResponseDTO> candidatos = listarCandidatosSugeridosUseCase.executar(idVaga, empresa.getId())
        return ResponseEntity.ok(ApiResponseDTO.sucesso(candidatos))
    }

    @PreAuthorize("hasRole('EMPRESA')")
    @GetMapping("/{idVaga}/candidatos/interessados")
    ResponseEntity<ApiResponseDTO<List<CandidatoPublicoResponseDTO>>> listarCandidatosInteressados(
            @PathVariable("idVaga") Long idVaga,
            @AuthenticationPrincipal Usuario usuarioAutenticado
    ) {
        Empresa empresa = empresaRepository.findByUsuarioEmail(usuarioAutenticado.getUsername())
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"))
        
        List<CandidatoPublicoResponseDTO> candidatos = listarCandidatosInteressadosUseCase.executar(idVaga, empresa.getId())
        return ResponseEntity.ok(ApiResponseDTO.sucesso(candidatos))
    }
}
