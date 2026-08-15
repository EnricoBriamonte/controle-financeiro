package com.portfolio.financas.service;

import com.portfolio.financas.exception.RecursoNaoEncontradoException;
import com.portfolio.financas.model.*;
import com.portfolio.financas.repository.CompraRepository;
import com.portfolio.financas.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private CartaoService cartaoService;

    @Autowired
    private SecurityUtils securityUtils;

    public List<Compra> listarTodas() {
        Usuario usuario = securityUtils.getUsuarioLogado();
        return compraRepository.findByUsuarioId(usuario.getId());
    }

    public Compra buscarPorId(Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Compra não encontrada com id " + id));
        validarPosse(compra);
        return compra;
    }

    /**
     * Salva a compra e gera automaticamente as parcelas correspondentes.
     *
     * A parte interessante: dividir um valor por N nem sempre dá uma conta
     * redonda (ex: R$ 100 em 3x = R$ 33,33 + R$ 33,33 + R$ 33,33 = R$ 99,99,
     * faltando 1 centavo). Resolvemos isso jogando a diferença na ÚLTIMA
     * parcela, técnica comum em sistemas financeiros reais.
     */
    public Compra salvar(Compra compra) {
        // Garante que o cartão informado pertence ao usuário logado
        CartaoCredito cartao = cartaoService.buscarPorId(compra.getCartao().getId());
        compra.setCartao(cartao);
        compra.setUsuario(securityUtils.getUsuarioLogado());

        int n = compra.getNumeroParcelas() == null ? 1 : compra.getNumeroParcelas();
        BigDecimal valorParcela = compra.getValorTotal()
                .divide(BigDecimal.valueOf(n), 2, RoundingMode.DOWN);
        BigDecimal somaParcelas = valorParcela.multiply(BigDecimal.valueOf(n));
        BigDecimal diferenca = compra.getValorTotal().subtract(somaParcelas);

        List<Parcela> parcelas = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            Parcela parcela = new Parcela();
            parcela.setNumero(i);
            // A última parcela absorve a diferença de arredondamento
            parcela.setValor(i == n ? valorParcela.add(diferenca) : valorParcela);
            parcela.setDataVencimento(calcularVencimento(compra.getData(), cartao.getDiaVencimento(), i));
            parcela.setPaga(false);
            parcela.setCompra(compra);
            parcelas.add(parcela);
        }
        compra.setParcelas(parcelas);

        return compraRepository.save(compra);
    }

    /**
     * Calcula a data de vencimento da parcela N, contando meses a partir
     * da data da compra e ajustando pro dia de vencimento do cartão.
     */
    private LocalDate calcularVencimento(LocalDate dataCompra, Integer diaVencimento, int numeroParcela) {
        LocalDate base = dataCompra.plusMonths(numeroParcela);
        int dia = Math.min(diaVencimento, base.lengthOfMonth());
        return base.withDayOfMonth(dia);
    }

    public void excluir(Long id) {
        Compra compra = buscarPorId(id);
        compraRepository.delete(compra);
    }

    private void validarPosse(Compra compra) {
        Usuario usuarioLogado = securityUtils.getUsuarioLogado();
        if (compra.getUsuario() == null || !compra.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new RecursoNaoEncontradoException("Compra não encontrada com id " + compra.getId());
        }
    }
}
