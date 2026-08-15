package com.portfolio.financas.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "parcelas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Parcela {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Ex: 1, 2, 3 — a posição desta parcela dentro do parcelamento. */
    private Integer numero;

    private BigDecimal valor;

    /** Mês em que essa parcela vence/fecha na fatura. */
    private LocalDate dataVencimento;

    private Boolean paga = false;

    @ManyToOne
    @JoinColumn(name = "compra_id")
    @JsonIgnore
    private Compra compra;
}
