package com.portfolio.financas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "cartoes_credito")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartaoCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do cartão é obrigatório")
    private String nome;

    private String banco;

    @NotNull(message = "O limite é obrigatório")
    private BigDecimal limite;

    @Min(1) @Max(31)
    @NotNull(message = "O dia de fechamento é obrigatório")
    private Integer diaFechamento;

    @Min(1) @Max(31)
    @NotNull(message = "O dia de vencimento é obrigatório")
    private Integer diaVencimento;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;
}
