package org.ugustavodev.msnucleo.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Visão pública da Vaga exibida nas listagens. Oculta o nome da empresa contratante antes do Match (Princípio do Anonimato).")
record VagaPublicoResponseDTO(
    @Schema(description = "ID único da vaga", example = "5")
    Long id,

    @Schema(description = "Título da vaga", example = "Pessoa Desenvolvedora Backend Groovy/Spring")
    String nome,

    @Schema(description = "Descrição das atividades e requisitos da vaga", example = "Atuação no desenvolvimento e manutenção de microsserviços RESTful com Spring Boot e PostgreSQL.")
    String descricao,

    @Schema(description = "Localidade ou modelo de trabalho", example = "São Paulo - SP (Remoto)")
    String local,

    @Schema(description = "Nível de senioridade da vaga", example = "Pleno")
    String nivel,

    @Schema(description = "ID da empresa responsável pela vaga", example = "1")
    Long empresaId
) {}
