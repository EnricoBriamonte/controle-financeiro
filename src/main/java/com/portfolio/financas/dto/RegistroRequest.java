package com.portfolio.financas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO (Data Transfer Object): representa exatamente o formato de dados
 * que entra/sai da API, separado da entidade do banco (Usuario).
 * Isso evita, por exemplo, que alguém consiga mandar um campo "id"
 * ou manipular dados que não deveriam ser expostos.
 */
@Data
public class RegistroRequest {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    private String senha;
}
