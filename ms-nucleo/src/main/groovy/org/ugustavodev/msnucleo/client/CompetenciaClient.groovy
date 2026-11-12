package org.ugustavodev.msnucleo.client

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.dto.response.CandidatosPorCompetenciaDTO
import org.ugustavodev.msnucleo.security.TokenService

@Component
class CompetenciaClient {

    private final RestClient restClient
    private final TokenService tokenService

    @Autowired
    CompetenciaClient(
            @Value('${microservices.competencia.url:http://localhost:8082}') String competenciaBaseUrl,
            TokenService tokenService) {
        this.restClient = RestClient.builder().baseUrl(competenciaBaseUrl).build()
        this.tokenService = tokenService
    }

    CompetenciaClient(RestClient restClient, TokenService tokenService) {
        this.restClient = restClient
        this.tokenService = tokenService
    }

    long contarCompetenciasCandidato(Usuario usuario, Long candidatoId) {
        if (usuario == null || candidatoId == null) {
            return 0L
        }
        return contarVinculos("/api/competencias/candidato/{id}", usuario, candidatoId)
    }

    long contarCompetenciasEmpresa(Usuario usuario, Long empresaId) {
        if (usuario == null || empresaId == null) {
            return 0L
        }
        return contarVinculos("/api/competencias/empresa/{id}", usuario, empresaId)
    }

    void excluirCompetenciasCandidato(Usuario usuario, Long candidatoId) {
        if (usuario == null) {
            throw new IllegalArgumentException("O usuário autenticado não pode ser nulo.")
        }
        if (candidatoId == null) {
            throw new IllegalArgumentException("O ID do candidato não pode ser nulo.")
        }

        String token = tokenService.gerarToken(usuario, candidatoId)
        restClient.delete()
                .uri("/api/competencias/candidato/{id}", candidatoId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${token}")
                .retrieve()
                .toBodilessEntity()
    }

    void excluirCompetenciasVaga(Usuario usuario, Long empresaId, Long vagaId) {
        if (usuario == null) {
            throw new IllegalArgumentException("O usuário autenticado não pode ser nulo.")
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("O ID da empresa não pode ser nulo.")
        }
        if (vagaId == null) {
            throw new IllegalArgumentException("O ID da vaga não pode ser nulo.")
        }

        String token = tokenService.gerarToken(usuario, empresaId)
        restClient.delete()
                .uri("/api/competencias/vaga/{id}", vagaId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${token}")
                .retrieve()
                .toBodilessEntity()
    }

    void excluirCompetenciasEmpresa(Usuario usuario, Long empresaId) {
        if (usuario == null) {
            throw new IllegalArgumentException("O usuário autenticado não pode ser nulo.")
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("O ID da empresa não pode ser nulo.")
        }

        String token = tokenService.gerarToken(usuario, empresaId)
        restClient.delete()
                .uri("/api/competencias/empresa/{id}", empresaId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${token}")
                .retrieve()
                .toBodilessEntity()
    }

    List<CandidatosPorCompetenciaDTO> listarCandidatosPorCompetencia(Usuario usuario, Long empresaId) {
        if (usuario == null || empresaId == null) {
            return Collections.emptyList()
        }

        try {
            String token = tokenService.gerarToken(usuario, empresaId)
            Map response = restClient.get()
                    .uri("/api/competencias/estatisticas/candidatos-por-competencia")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer ${token}")
                    .retrieve()
                    .body(Map)

            Object dadosObj = response?.get("dados")
            if (!(dadosObj instanceof List)) {
                return Collections.emptyList()
            }

            List<?> listaDados = (List<?>) dadosObj
            return listaDados.findAll { it instanceof Map }.collect { Object item ->
                Map mapa = (Map) item
                Long compId = (mapa.get("competenciaId") as Number)?.longValue()
                String nome = mapa.get("nomeCompetencia") as String
                Long total = (mapa.get("totalCandidatos") as Number)?.longValue() ?: 0L
                new CandidatosPorCompetenciaDTO(compId, nome, total)
            }
        } catch (RestClientException ignored) {
            return Collections.emptyList()
        }
    }

    private long contarVinculos(String uriTemplate, Usuario usuario, Long perfilId) {
        try {
            String token = tokenService.gerarToken(usuario, perfilId)
            Map response = restClient.get()
                    .uri(uriTemplate, perfilId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer ${token}")
                    .retrieve()
                    .body(Map)

            Object dadosObj = response?.get("dados")
            if (dadosObj instanceof List) {
                return ((List<?>) dadosObj).size()
            }
            return 0L
        } catch (RestClientException ignored) {
            return 0L
        }
    }

}
