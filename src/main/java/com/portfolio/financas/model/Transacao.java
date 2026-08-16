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
     * Conta bancária/carteira onde a transação ocorreu. Opcional, pra não
     * obrigar quem não quiser detalhar contas a preencher isso.
     */
    @ManyToOne
    @JoinColumn(name = "conta_id")
    private Conta conta;

    /** Se já foi de fato recebida/paga, ou ainda está pendente. */
    @Enumerated(EnumType.STRING)
    private StatusTransacao status = StatusTransacao.CONFIRMADA;

    /** Se é um gasto/receita fixo (se repete todo mês) ou variável. */
    @Enumerated(EnumType.STRING)
    private NaturezaTransacao natureza = NaturezaTransacao.VARIAVEL;

    /** Ex: "Cartão de crédito", "Pix", "Dinheiro". Texto livre, opcional. */
    private String formaPagamento;

    private String observacao;

    /**
     * Se essa transação foi gerada automaticamente a partir de uma
     * Recorrencia (ex: assinatura mensal), guardamos a referência aqui.
     * Isso é o que permite ao sistema saber "já gerei o lançamento de
     * Netflix desse mês?" antes de gerar de novo.
     */
    @ManyToOne
    @JoinColumn(name = "recorrencia_origem_id")
    @JsonIgnore
    private Recorrencia recorrenciaOrigem;

    /**
     * Dono da transação, definido automaticamente pelo servidor a partir
     * do usuário logado — por isso fica oculto na entrada e saída do JSON.
     */
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;
}
