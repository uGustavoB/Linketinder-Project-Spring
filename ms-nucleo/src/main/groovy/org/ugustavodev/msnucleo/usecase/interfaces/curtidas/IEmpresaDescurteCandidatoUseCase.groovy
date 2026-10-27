package org.ugustavodev.msnucleo.usecase.interfaces.curtidas

interface IEmpresaDescurteCandidatoUseCase {
    void executar(Long idEmpresa, Long idCandidato, Long idVaga)
}
