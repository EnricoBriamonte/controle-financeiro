package com.portfolio.financas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representa um objetivo financeiro (ex: "Comprar notebook — R$ 5.000").
 * O progresso é calculado como valorAtual / valorMeta.
 */
@Entity
@Table(name = "metas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome da meta é obrigatório")
    private String nome;

    @NotNull(message = "O valor da meta é obrigatório")
    @Positive(message = "O valor da meta deve ser maior que zero")
    private BigDecimal valorMeta;

    private BigDecimal valorAtual = BigDecimal.ZERO;

    /** Data limite para atingir a meta (opcional). */
    private LocalDate prazo;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;
}
