package com.portfolio.financas;

import com.portfolio.financas.model.CartaoCredito;
import com.portfolio.financas.model.Compra;
import com.portfolio.financas.model.Usuario;
import com.portfolio.financas.repository.CompraRepository;
import com.portfolio.financas.security.SecurityUtils;
import com.portfolio.financas.service.CartaoService;
import com.portfolio.financas.service.CompraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Testa a divisão de uma compra em parcelas — incluindo o caso "chato"
 * onde a divisão não é exata e sobra centavos (ex: R$ 100 em 3x).
 */
class CompraServiceTest {

    @Mock private CompraRepository compraRepository;
    @Mock private CartaoService cartaoService;
    @Mock private SecurityUtils securityUtils;
    @InjectMocks private CompraService compraService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveDividirValorEmParcelasIguaisQuandoDivisaoEExata() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        CartaoCredito cartao = new CartaoCredito();
        cartao.setId(1L);
        cartao.setDiaVencimento(10);
        cartao.setUsuario(usuario);

        when(securityUtils.getUsuarioLogado()).thenReturn(usuario);
        when(cartaoService.buscarPorId(1L)).thenReturn(cartao);
        when(compraRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Compra compra = new Compra();
        compra.setDescricao("Notebook");
        compra.setValorTotal(new BigDecimal("300.00"));
        compra.setData(LocalDate.of(2026, 8, 10));
        compra.setNumeroParcelas(3);
        compra.setCartao(cartao);

        Compra salva = compraService.salvar(compra);

        assertEquals(3, salva.getParcelas().size());
        assertEquals(new BigDecimal("100.00"), salva.getParcelas().get(0).getValor());
        assertEquals(new BigDecimal("100.00"), salva.getParcelas().get(2).getValor());
    }

    @Test
    void deveJogarDiferencaDeArredondamentoNaUltimaParcela() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        CartaoCredito cartao = new CartaoCredito();
        cartao.setId(1L);
        cartao.setDiaVencimento(10);
        cartao.setUsuario(usuario);

        when(securityUtils.getUsuarioLogado()).thenReturn(usuario);
        when(cartaoService.buscarPorId(1L)).thenReturn(cartao);
        when(compraRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Compra compra = new Compra();
        compra.setDescricao("Fone de ouvido");
        compra.setValorTotal(new BigDecimal("100.00"));
        compra.setData(LocalDate.of(2026, 8, 10));
        compra.setNumeroParcelas(3); // 100 / 3 = 33.33... uma divisão não exata
        compra.setCartao(cartao);

        Compra salva = compraService.salvar(compra);

        BigDecimal somaTotal = salva.getParcelas().stream()
                .map(p -> p.getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // As duas primeiras parcelas ficam com o valor "redondo" pra baixo...
        assertEquals(new BigDecimal("33.33"), salva.getParcelas().get(0).getValor());
        assertEquals(new BigDecimal("33.33"), salva.getParcelas().get(1).getValor());
        // ...e a última absorve o centavo que sobrou, pra fechar exatamente o valor total
        assertEquals(new BigDecimal("33.34"), salva.getParcelas().get(2).getValor());
        assertEquals(new BigDecimal("100.00"), somaTotal);
    }
}
