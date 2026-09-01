package org.ugustavodev.msnucleo.dto.response

record EmpresaResponseDTO(
        Long id,
        String nome,
        String cnpj,
        String email,
        String pais,
        String estado,
        String cep,
        String descricao
) {
}
