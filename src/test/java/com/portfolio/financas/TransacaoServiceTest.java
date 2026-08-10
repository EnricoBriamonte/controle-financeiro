package com.portfolio.financas;

import com.portfolio.financas.model.Categoria;
import com.portfolio.financas.model.TipoTransacao;
import com.portfolio.financas.model.Transacao;
import com.portfolio.financas.model.Usuario;
import com.portfolio.financas.repository.TransacaoRepository;
import com.portfolio.financas.security.SecurityUtils;
import com.portfolio.financas.service.TransacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Teste "unitário": testamos só a lógica do Service, sem subir banco de
 * dados nem contexto de segurança de verdade. TransacaoRepository e
 * SecurityUtils são substituídos por "dublês" (mocks) que devolvem
 * dados fixos controlados pelo teste.
 */
class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private TransacaoService transacaoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCalcularSaldoMensalCorretamente() {
        Usuario usuario = new Usuario(1L, "Maria", "maria@email.com", "hash-fake");
        Categoria categoria = new Categoria(1L, "Salário", "", null, usuario);

        Transacao receita = new Transacao(1L, "Salário de agosto", new BigDecimal("3000.00"),
                LocalDate.of(2026, 8, 5), TipoTransacao.RECEITA, categoria, usuario);
        Transacao despesa = new Transacao(2L, "Aluguel", new BigDecimal("1200.00"),
                LocalDate.of(2026, 8, 10), TipoTransacao.DESPESA, categoria, usuario);

        YearMonth mes = YearMonth.of(2026, 8);

        when(securityUtils.getUsuarioLogado()).thenReturn(usuario);
        when(transacaoRepository.findByUsuarioIdAndDataBetween(usuario.getId(), mes.atDay(1), mes.atEndOfMonth()))
                .thenReturn(List.of(receita, despesa));

        BigDecimal saldo = transacaoService.calcularSaldoMensal(mes);

        assertEquals(new BigDecimal("1800.00"), saldo);
    }
}
