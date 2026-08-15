package com.portfolio.financas.service;

import com.portfolio.financas.exception.RecursoNaoEncontradoException;
import com.portfolio.financas.model.CartaoCredito;
import com.portfolio.financas.model.Compra;
import com.portfolio.financas.model.Usuario;
import com.portfolio.financas.repository.CartaoRepository;
import com.portfolio.financas.repository.CompraRepository;
import com.portfolio.financas.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class CartaoService {

    @Autowired
    private CartaoRepository cartaoRepository;

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private SecurityUtils securityUtils;

    public List<CartaoCredito> listarTodos() {
        Usuario usuario = securityUtils.getUsuarioLogado();
        return cartaoRepository.findByUsuarioId(usuario.getId());
    }

    public CartaoCredito buscarPorId(Long id) {
        CartaoCredito cartao = cartaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cartão não encontrado com id " + id));
        validarPosse(cartao);
        return cartao;
    }

    public CartaoCredito salvar(CartaoCredito cartao) {
        cartao.setUsuario(securityUtils.getUsuarioLogado());
        return cartaoRepository.save(cartao);
    }

    public void excluir(Long id) {
        CartaoCredito cartao = buscarPorId(id);
        cartaoRepository.delete(cartao);
    }

    /**
     * Calcula quanto do limite do cartão já está comprometido, somando
     * todas as parcelas ainda não pagas de todas as compras desse cartão.
     */
    public Map<String, BigDecimal> resumoUso(Long cartaoId) {
        CartaoCredito cartao = buscarPorId(cartaoId);
        List<Compra> compras = compraRepository.findByCartaoId(cartaoId);

        BigDecimal utilizado = compras.stream()
                .flatMap(c -> c.getParcelas().stream())
                .filter(p -> !Boolean.TRUE.equals(p.getPaga()))
                .map(p -> p.getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal disponivel = cartao.getLimite().subtract(utilizado);

        return Map.of(
                "limite", cartao.getLimite(),
                "utilizado", utilizado,
                "disponivel", disponivel
        );
    }

    private void validarPosse(CartaoCredito cartao) {
        Usuario usuarioLogado = securityUtils.getUsuarioLogado();
        if (cartao.getUsuario() == null || !cartao.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new RecursoNaoEncontradoException("Cartão não encontrado com id " + cartao.getId());
        }
    }
}
