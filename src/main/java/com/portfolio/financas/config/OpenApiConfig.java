package com.portfolio.financas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Customiza a página de documentação gerada pelo springdoc-openapi
 * (disponível em /swagger-ui.html), trocando o título genérico
 * por informações reais do projeto.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Controle Financeiro Pessoal API")
                        .description("API REST para gerenciamento de receitas, despesas e relatórios financeiros")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Seu Nome")
                                .url("https://github.com/SEU-USUARIO")));
    }
}
