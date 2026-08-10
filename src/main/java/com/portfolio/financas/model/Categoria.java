package com.portfolio.financas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Uma Categoria agrupa transações (ex: "Alimentação", "Salário", "Transporte").
 *
 * @Entity diz ao Spring/Hibernate: "isso vira uma tabela no banco".
 * @Data (Lombok) gera automaticamente getters, setters, toString, equals e hashCode.
 */
@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome da categoria é obrigatório")
    @Column(nullable = false, unique = true)
    private String nome;

    private String descricao;

    /**
     * mappedBy = "categoria" indica que quem "dona" do relacionamento
     * é a classe Transacao (é lá que fica a chave estrangeira).
     */
    /**
     * @JsonIgnore evita dois problemas: 1) recursão infinita (categoria tem
     * transações, cada transação tem categoria, que tem transações...) e
     * 2) expor dados que o cliente não precisa ver aqui.
     */
    @OneToMany(mappedBy = "categoria")
    @JsonIgnore
    private List<Transacao> transacoes;

    /**
     * Dono da categoria, definido automaticamente pelo servidor a partir
     * do usuário logado — por isso fica oculto na entrada e saída do JSON.
     */
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;
}
