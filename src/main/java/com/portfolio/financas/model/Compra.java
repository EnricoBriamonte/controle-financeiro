package com.portfolio.financas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Uma compra feita no cartão, que pode ser dividida em N parcelas.
 * Ao salvar, o sistema gera automaticamente as Parcelas correspondentes
 * (ver CompraService) — essa é a parte que demonstra lógica de programação
 * real: dividir o valor, distribuir centavos residuais, calcular datas.
 */
@Entity
@Table(name = "compras_cartao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

    @NotNull(message = "O valor total é obrigatório")
    @Positive(message = "O valor deve ser maior que zero")
    private BigDecimal valorTotal;

    @NotNull(message = "A data da compra é obrigatória")
    private LocalDate data;

    @Min(value = 1, message = "Deve ter ao menos 1 parcela")
    @Max(value = 48, message = "Máximo de 48 parcelas")
    private Integer numeroParcelas = 1;

    @ManyToOne
    @JoinColumn(name = "cartao_id")
    @NotNull(message = "O cartão é obrigatório")
    private CartaoCredito cartao;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Parcela> parcelas;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;
}
