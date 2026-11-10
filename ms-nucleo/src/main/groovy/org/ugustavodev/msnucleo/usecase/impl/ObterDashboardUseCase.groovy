package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.ugustavodev.msnucleo.client.CompetenciaClient
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.dto.response.CandidatosPorCompetenciaDTO
import org.ugustavodev.msnucleo.dto.response.DashboardCandidatoResponseDTO
import org.ugustavodev.msnucleo.dto.response.DashboardEmpresaResponseDTO
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.repositories.CurtidaRepository
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.repositories.VagaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IObterDashboardUseCase

@Service
class ObterDashboardUseCase implements IObterDashboardUseCase {

    private final CandidatoRepository candidatoRepository
    private final EmpresaRepository empresaRepository
    private final VagaRepository vagaRepository
    private final CurtidaRepository curtidaRepository
    private final CompetenciaClient competenciaClient

    ObterDashboardUseCase(
            CandidatoRepository candidatoRepository,
            EmpresaRepository empresaRepository,
            VagaRepository vagaRepository,
            CurtidaRepository curtidaRepository,
            CompetenciaClient competenciaClient) {
        this.candidatoRepository = candidatoRepository
        this.empresaRepository = empresaRepository
        this.vagaRepository = vagaRepository
        this.curtidaRepository = curtidaRepository
        this.competenciaClient = competenciaClient
    }

    @Override
    @Transactional(readOnly = true)
    Object executar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("O usuário autenticado não pode ser nulo.")
        }

        if (usuario.role == Role.CANDIDATO) {
            Candidato candidato = candidatoRepository.findByUsuarioEmail(usuario.email)
                    .orElseThrow { new NoSuchElementException("Perfil de candidato não encontrado.") }

            long totalVagasSugeridas = vagaRepository.countVagasSugeridasParaCandidato(candidato.id)
            long totalCurtidasEnviadas = curtidaRepository.countByCandidatoIdAndCandidatoCurtiuEmIsNotNull(candidato.id)
            long totalCurtidasRecebidas = curtidaRepository.countByCandidatoIdAndEmpresaCurtiuEmIsNotNull(candidato.id)
            long totalMatches = curtidaRepository.countByCandidatoIdAndCandidatoCurtiuEmIsNotNullAndEmpresaCurtiuEmIsNotNull(candidato.id)
            long totalCompetencias = competenciaClient.contarCompetenciasCandidato(usuario, candidato.id)

            return new DashboardCandidatoResponseDTO(
                    "CANDIDATO",
                    totalVagasSugeridas,
                    totalCurtidasEnviadas,
                    totalCurtidasRecebidas,
                    totalMatches,
                    totalCompetencias
            )
        }

        if (usuario.role == Role.EMPRESA) {
            Empresa empresa = empresaRepository.findByUsuarioEmail(usuario.email)
                    .orElseThrow { new NoSuchElementException("Perfil de empresa não encontrado.") }

            long totalVagasAtivas = vagaRepository.countByEmpresaId(empresa.id)
            long totalCurtidasRecebidas = curtidaRepository.countByVagaEmpresaIdAndCandidatoCurtiuEmIsNotNull(empresa.id)
            long totalCurtidasEnviadas = curtidaRepository.countByVagaEmpresaIdAndEmpresaCurtiuEmIsNotNull(empresa.id)
            long totalMatches = curtidaRepository.countByVagaEmpresaIdAndCandidatoCurtiuEmIsNotNullAndEmpresaCurtiuEmIsNotNull(empresa.id)
            long totalCompetencias = competenciaClient.contarCompetenciasEmpresa(usuario, empresa.id)
            List<CandidatosPorCompetenciaDTO> candidatosPorCompetencia = competenciaClient.listarCandidatosPorCompetencia(usuario, empresa.id)

            return new DashboardEmpresaResponseDTO(
                    "EMPRESA",
                    totalVagasAtivas,
                    totalCurtidasRecebidas,
                    totalCurtidasEnviadas,
                    totalMatches,
                    totalCompetencias,
                    candidatosPorCompetencia
            )
        }

        throw new NoSuchElementException("Perfil não encontrado.")
    }

}
