package org.ugustavodev.msnucleo.dto.response

record PerfilEmpresaDTO(
        Long id,
        String tipoUsuario,
        String nome,
        String cnpj,
        String email,
        String pais,
        String estado,
        String cep,
        String descricao
) {}
