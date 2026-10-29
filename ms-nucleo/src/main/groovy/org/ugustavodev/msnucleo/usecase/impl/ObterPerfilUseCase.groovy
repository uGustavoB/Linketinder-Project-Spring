package org.ugustavodev.msnucleo.usecase.impl

import org.springframework.stereotype.Service
import org.ugustavodev.msnucleo.domain.Usuario
import org.ugustavodev.msnucleo.domain.enums.Role
import org.ugustavodev.msnucleo.dto.response.FormacaoResponseDTO
import org.ugustavodev.msnucleo.dto.response.PerfilCandidatoDTO
import org.ugustavodev.msnucleo.dto.response.PerfilEmpresaDTO
import org.ugustavodev.msnucleo.mapper.FormacaoMapper
import org.ugustavodev.msnucleo.model.Candidato
import org.ugustavodev.msnucleo.model.Empresa
import org.ugustavodev.msnucleo.repositories.CandidatoRepository
import org.ugustavodev.msnucleo.repositories.EmpresaRepository
import org.ugustavodev.msnucleo.usecase.interfaces.IObterPerfilUseCase

@Service
class ObterPerfilUseCase implements IObterPerfilUseCase {

    private final CandidatoRepository candidatoRepository
    private final EmpresaRepository empresaRepository
    private final FormacaoMapper formacaoMapper

    ObterPerfilUseCase(
            CandidatoRepository candidatoRepository,
            EmpresaRepository empresaRepository,
            FormacaoMapper formacaoMapper) {
        this.candidatoRepository = candidatoRepository
        this.empresaRepository = empresaRepository
        this.formacaoMapper = formacaoMapper
    }

    @Override
    Object executar(Usuario usuario) {
        if (usuario.role == Role.CANDIDATO) {
            Optional<Candidato> candidatoOpt = candidatoRepository.findByUsuarioEmail(usuario.email)

            if (candidatoOpt.isPresent()) {
                Candidato candidato = candidatoOpt.get()
                List<FormacaoResponseDTO> formacoesDTO = candidato.formacoes?.stream()
                        ?.map(formacaoMapper::toResponseDTO)
                        ?.toList() ?: new ArrayList<>()

                return new PerfilCandidatoDTO(
                        candidato.id,
                        "CANDIDATO",
                        candidato.nome,
                        candidato.sobrenome,
                        candidato.dataNascimento,
                        usuario.email,
                        candidato.cpf,
                        candidato.pais,
                        candidato.estado,
                        candidato.cep,
                        candidato.descricao,
                        formacoesDTO
                )
            }
        } else if (usuario.role == Role.EMPRESA) {
            Optional<Empresa> empresaOpt = empresaRepository.findByUsuarioEmail(usuario.email)

            if (empresaOpt.isPresent()) {
                Empresa empresa = empresaOpt.get()
                return new PerfilEmpresaDTO(
                        empresa.id,
                        "EMPRESA",
                        empresa.nome,
                        empresa.cnpj,
                        usuario.email,
                        empresa.pais,
                        empresa.estado,
                        empresa.cep,
                        empresa.descricao
                )
            }
        }
        
        return null
    }

}
