package com.portfolio.financas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AtualizarPerfilRequest {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;
}
