package com.portfolio.financas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Define quanto o usuário planeja gastar numa categoria em um mês específico
 * (ex: "Alimentação — R$ 600 — Agosto/2026"). O sistema compara isso com o
 * total real gasto naquela categoria/mês, calculado a partir das Transacoes.
 */
@Entity
@Table(name = "orcamentos", uniqueConstraints = @UniqueConstraint(columnNames = {"categoria_id", "mes", "usuario_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    @NotNull(message = "A categoria é obrigatória")
    private Categoria categoria;

    @NotNull(message = "O valor planejado é obrigatório")
    @Positive(message = "O valor deve ser maior que zero")
    private BigDecimal valorPlanejado;

    /**
     * Mês/ano de referência, formato "yyyy-MM" (ex: "2026-08").
     * Guardado como texto para simplificar consultas.
     */
    @NotNull(message = "O mês de referência é obrigatório")
    private String mes;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;
}
