package com.portfolio.financas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal: é ela que "liga" a aplicação Spring Boot.
 * A anotação @SpringBootApplication já configura tudo que precisamos
 * (varre os pacotes, ativa o servidor web, configura o banco, etc).
 */
@SpringBootApplication
public class FinancasApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinancasApplication.class, args);
    }

}
