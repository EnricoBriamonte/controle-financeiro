package com.portfolio.financas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Representa um lançamento que se repete todo mês (ex: "Netflix — R$ 45,90
 * — todo dia 15"). Não gera as transações sozinha: quem faz isso é o
 * RecorrenciaService, quando o usuário pede pra "gerar os lançamentos do mês".
 */
@Entity
@Table(name = "recorrencias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

    @NotNull(message = "O valor é obrigatório")
    @Positive(message = "O valor deve ser maior que zero")
    private BigDecimal valor;

    @Min(value = 1, message = "O dia do mês deve ser entre 1 e 31")
    @Max(value = 31, message = "O dia do mês deve ser entre 1 e 31")
    @NotNull(message = "O dia do mês é obrigatório")
    private Integer diaDoMes;

    @NotNull(message = "O tipo é obrigatório")
    @Enumerated(EnumType.STRING)
    private TipoTransacao tipo;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    @NotNull(message = "A categoria é obrigatória")
    private Categoria categoria;

    /** Permite "pausar" uma recorrência sem precisar excluir o histórico dela. */
    private Boolean ativa = true;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;
}
