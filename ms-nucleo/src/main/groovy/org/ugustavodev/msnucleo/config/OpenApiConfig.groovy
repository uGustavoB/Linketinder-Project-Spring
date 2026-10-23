package org.ugustavodev.msnucleo.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    static final String ESQUEMA_BEARER = "bearerAuth"
    static final String ESQUEMA_COOKIE = "cookieAuth"

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LinkeTinder — API do Microsserviço Núcleo (ms-nucleo)")
                        .version("0.0.1-SNAPSHOT")
                        .description("""Documentação interativa da API do microsserviço **ms-nucleo** da plataforma **LinkeTinder**.

### Autenticação no Swagger UI
1. **Via Cookie HTTP-only (`auth_token`)**: Execute `POST /api/auth/login` diretamente nesta interface; o navegador enviará o cookie automaticamente nas demais requisições.
2. **Via Bearer Token (`Authorization`)**: Utilize o botão **Authorize** informando o token JWT.""")
                        .contact(new Contact()
                                .name("Gustavo Gabriel")
                                .url("https://github.com/uGustavoB")
                        )
                )
                .addSecurityItem(new SecurityRequirement()
                        .addList(ESQUEMA_BEARER)
                        .addList(ESQUEMA_COOKIE)
                )
                .components(new Components()
                        .addSecuritySchemes(ESQUEMA_BEARER, new SecurityScheme()
                                .name(ESQUEMA_BEARER)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Informe o token JWT para envio no cabeçalho Authorization: Bearer <token>.")
                        )
                        .addSecuritySchemes(ESQUEMA_COOKIE, new SecurityScheme()
                                .name("auth_token")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .description("Cookie HTTP-only (auth_token) definido automaticamente ao realizar login em POST /api/auth/login.")
                        )
                )
    }
}
