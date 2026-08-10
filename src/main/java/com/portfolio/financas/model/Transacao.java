package com.portfolio.financas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.math.BigDecimal;

/**
 * Representa uma movimentação financeira: uma receita ou uma despesa.
 * Usamos BigDecimal (não double) para valores em dinheiro, pra evitar
 * erros de arredondamento em ponto flutuante.
 */
@Entity
@Table(name = "transacoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

    @NotNull(message = "O valor é obrigatório")
    @Positive(message = "O valor deve ser maior que zero")
    private BigDecimal valor;

    @NotNull(message = "A data é obrigatória")
    private LocalDate data;

    @NotNull(message = "O tipo (RECEITA ou DESPESA) é obrigatório")
    @Enumerated(EnumType.STRING)
    private TipoTransacao tipo;

    /**
     * Relacionamento muitos-para-um: várias transações podem
     * pertencer à mesma categoria.
     */
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    @NotNull(message = "A categoria é obrigatória")
    private Categoria categoria;

    /**
     * Dono da transação, definido automaticamente pelo servidor a partir
     * do usuário logado — por isso fica oculto na entrada e saída do JSON.
     */
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;
}
