package com.portfolio.financas.service;

import com.portfolio.financas.exception.RecursoNaoEncontradoException;
import com.portfolio.financas.model.*;
import com.portfolio.financas.repository.OrcamentoRepository;
import com.portfolio.financas.repository.TransacaoRepository;
import com.portfolio.financas.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrcamentoService {

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private SecurityUtils securityUtils;

    public List<Orcamento> listarTodos() {
        Usuario usuario = securityUtils.getUsuarioLogado();
        return orcamentoRepository.findByUsuarioId(usuario.getId());
    }

    public Orcamento salvar(Orcamento orcamento) {
        orcamento.setUsuario(securityUtils.getUsuarioLogado());
        return orcamentoRepository.save(orcamento);
    }

    public void excluir(Long id) {
        Orcamento orcamento = orcamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Orçamento não encontrado com id " + id));
        validarPosse(orcamento);
        orcamentoRepository.delete(orcamento);
    }

    /**
     * O coração do módulo de orçamento: para cada categoria planejada no mês,
     * calcula quanto já foi de fato gasto (somando as despesas confirmadas)
     * e devolve planejado x realizado, pra o front mostrar a barra de progresso.
     */
    public List<Map<String, Object>> compararComGastoReal(YearMonth mes) {
        Usuario usuario = securityUtils.getUsuarioLogado();
        String mesStr = mes.toString();

        List<Orcamento> orcamentosDoMes = orcamentoRepository.findByUsuarioIdAndMes(usuario.getId(), mesStr);

        List<Transacao> despesasDoMes = transacaoRepository
                .findByUsuarioIdAndDataBetween(usuario.getId(), mes.atDay(1), mes.atEndOfMonth())
                .stream()
                .filter(t -> t.getTipo() == TipoTransacao.DESPESA)
                .collect(Collectors.toList());

        return orcamentosDoMes.stream().map(orc -> {
            BigDecimal gasto = despesasDoMes.stream()
                    .filter(t -> t.getCategoria().getId().equals(orc.getCategoria().getId()))
                    .map(Transacao::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            boolean estourou = gasto.compareTo(orc.getValorPlanejado()) > 0;

            return Map.<String, Object>of(
                    "categoria", orc.getCategoria().getNome(),
                    "planejado", orc.getValorPlanejado(),
                    "gasto", gasto,
                    "estourou", estourou
            );
        }).collect(Collectors.toList());
    }

    private void validarPosse(Orcamento orcamento) {
        Usuario usuarioLogado = securityUtils.getUsuarioLogado();
        if (orcamento.getUsuario() == null || !orcamento.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new RecursoNaoEncontradoException("Orçamento não encontrado com id " + orcamento.getId());
        }
    }
}
