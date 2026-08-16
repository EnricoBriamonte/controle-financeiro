package com.portfolio.financas;

import com.portfolio.financas.model.CartaoCredito;
import com.portfolio.financas.model.Compra;
import com.portfolio.financas.model.Parcela;
import com.portfolio.financas.model.Usuario;
import com.portfolio.financas.repository.CartaoRepository;
import com.portfolio.financas.repository.CompraRepository;
import com.portfolio.financas.security.SecurityUtils;
import com.portfolio.financas.service.CartaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class CartaoServiceTest {

    @Mock private CartaoRepository cartaoRepository;
    @Mock private CompraRepository compraRepository;
    @Mock private SecurityUtils securityUtils;
    @InjectMocks private CartaoService cartaoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCalcularLimiteDisponivelDescontandoParcelasNaoPagas() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        CartaoCredito cartao = new CartaoCredito();
        cartao.setId(1L);
        cartao.setLimite(new BigDecimal("5000.00"));
        cartao.setUsuario(usuario);

        Parcela paga = new Parcela();
        paga.setValor(new BigDecimal("100.00"));
        paga.setPaga(true);

        Parcela pendente1 = new Parcela();
        pendente1.setValor(new BigDecimal("300.00"));
        pendente1.setPaga(false);

        Parcela pendente2 = new Parcela();
        pendente2.setValor(new BigDecimal("950.00"));
        pendente2.setPaga(false);

        Compra compra1 = new Compra();
        compra1.setParcelas(List.of(paga, pendente1));

        Compra compra2 = new Compra();
        compra2.setParcelas(List.of(pendente2));

        when(securityUtils.getUsuarioLogado()).thenReturn(usuario);
        when(cartaoRepository.findById(1L)).thenReturn(Optional.of(cartao));
        when(compraRepository.findByCartaoId(1L)).thenReturn(List.of(compra1, compra2));

        Map<String, BigDecimal> resumo = cartaoService.resumoUso(1L);

        // Só as parcelas NÃO pagas contam como "utilizado" (300 + 950 = 1250)
        assertEquals(new BigDecimal("1250.00"), resumo.get("utilizado"));
        assertEquals(new BigDecimal("3750.00"), resumo.get("disponivel"));
    }
}
