package com.portfolio.financas.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Customiza a página de documentação gerada pelo springdoc-openapi
 * (disponível em /swagger-ui.html), trocando o título genérico
 * por informações reais do projeto.
 */
@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA_JWT = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Controle Financeiro Pessoal API")
                        .description("API REST para gerenciamento de receitas, despesas e relatórios financeiros")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Seu Nome")
                                .url("https://github.com/SEU-USUARIO")))
                // Isso é o que faz o botão "Authorize" (cadeado) aparecer no Swagger UI,
                // permitindo colar o token JWT e testar as rotas protegidas por lá.
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_JWT))
                .components(new Components()
                        .addSecuritySchemes(ESQUEMA_JWT, new SecurityScheme()
                                .name(ESQUEMA_JWT)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
