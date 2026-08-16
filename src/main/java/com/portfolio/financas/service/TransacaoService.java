package com.portfolio.financas.service;

import com.portfolio.financas.exception.RecursoNaoEncontradoException;
import com.portfolio.financas.model.StatusTransacao;
import com.portfolio.financas.model.TipoTransacao;
import com.portfolio.financas.model.Transacao;
import com.portfolio.financas.model.Usuario;
import com.portfolio.financas.repository.TransacaoRepository;
import com.portfolio.financas.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private CategoriaService categoriaService;

    public List<Transacao> listarTodas() {
        Usuario usuario = securityUtils.getUsuarioLogado();
        return transacaoRepository.findByUsuarioId(usuario.getId());
    }

    public Transacao buscarPorId(Long id) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Transação não encontrada com id " + id));
        validarPosse(transacao);
        return transacao;
    }

    public Transacao salvar(Transacao transacao) {
        // Garante que a categoria informada pertence ao usuário logado
        // (buscarPorId já lança erro se não pertencer)
        categoriaService.buscarPorId(transacao.getCategoria().getId());
        transacao.setUsuario(securityUtils.getUsuarioLogado());
        return transacaoRepository.save(transacao);
    }

    public Transacao atualizar(Long id, Transacao dadosNovos) {
        Transacao existente = buscarPorId(id); // já valida posse
        existente.setDescricao(dadosNovos.getDescricao());
        existente.setValor(dadosNovos.getValor());
        existente.setData(dadosNovos.getData());
        existente.setTipo(dadosNovos.getTipo());
        existente.setCategoria(dadosNovos.getCategoria());
        return transacaoRepository.save(existente);
    }

    public void excluir(Long id) {
        Transacao transacao = buscarPorId(id); // já valida posse
        transacaoRepository.delete(transacao);
    }

    /**
     * Calcula o saldo (receitas - despesas) de um mês específico,
     * considerando só as transações do usuário logado.
     */
    public BigDecimal calcularSaldoMensal(YearMonth mes) {
        Usuario usuario = securityUtils.getUsuarioLogado();
        List<Transacao> transacoesDoMes = buscarTransacoesDoMes(usuario.getId(), mes);

        BigDecimal receitas = somaPorTipo(transacoesDoMes, TipoTransacao.RECEITA);
        BigDecimal despesas = somaPorTipo(transacoesDoMes, TipoTransacao.DESPESA);

        return receitas.subtract(despesas);
    }

    public Map<String, BigDecimal> gastosPorCategoria(YearMonth mes) {
        Usuario usuario = securityUtils.getUsuarioLogado();
        List<Transacao> transacoesDoMes = buscarTransacoesDoMes(usuario.getId(), mes);

        return transacoesDoMes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.DESPESA)
                .collect(Collectors.groupingBy(
                        t -> t.getCategoria().getNome(),
                        Collectors.reducing(BigDecimal.ZERO, Transacao::getValor, BigDecimal::add)
                ));
    }

    /**
     * Gera um CSV simples (separado por ponto-e-vírgula, padrão do Excel
     * em português) com as transações do mês pedido.
     */
    public byte[] exportarCsv(YearMonth mes) {
        Usuario usuario = securityUtils.getUsuarioLogado();
        List<Transacao> transacoes = buscarTransacoesDoMes(usuario.getId(), mes);

        StringBuilder sb = new StringBuilder();
        sb.append("Data;Descrição;Categoria;Tipo;Status;Valor\n");

        for (Transacao t : transacoes) {
            sb.append(t.getData()).append(";")
                    .append(t.getDescricao().replace(";", ",")).append(";")
                    .append(t.getCategoria() != null ? t.getCategoria().getNome() : "").append(";")
                    .append(t.getTipo()).append(";")
                    .append(t.getStatus()).append(";")
                    .append(t.getValor()).append("\n");
        }

        return sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private List<Transacao> buscarTransacoesDoMes(Long usuarioId, YearMonth mes) {
        LocalDate inicio = mes.atDay(1);
        LocalDate fim = mes.atEndOfMonth();
        return transacaoRepository.findByUsuarioIdAndDataBetween(usuarioId, inicio, fim);
    }

    /**
     * Busca com filtros combináveis — todos opcionais. Como o volume de dados
     * de um app pessoal é pequeno, filtramos em memória (via Stream) depois
     * de trazer as transações do usuário, em vez de montar uma query dinâmica
     * complexa no banco — mais simples de ler e manter.
     */
    public List<Transacao> buscarComFiltros(LocalDate inicio, LocalDate fim, Long categoriaId,
                                             Long contaId, TipoTransacao tipo, StatusTransacao status,
                                             BigDecimal valorMin, BigDecimal valorMax, String descricaoContem) {
        Usuario usuario = securityUtils.getUsuarioLogado();
        List<Transacao> todas = transacaoRepository.findByUsuarioId(usuario.getId());

        return todas.stream()
                .filter(t -> inicio == null || !t.getData().isBefore(inicio))
                .filter(t -> fim == null || !t.getData().isAfter(fim))
                .filter(t -> categoriaId == null || t.getCategoria().getId().equals(categoriaId))
                .filter(t -> contaId == null || (t.getConta() != null && t.getConta().getId().equals(contaId)))
                .filter(t -> tipo == null || t.getTipo() == tipo)
                .filter(t -> status == null || t.getStatus() == status)
                .filter(t -> valorMin == null || t.getValor().compareTo(valorMin) >= 0)
                .filter(t -> valorMax == null || t.getValor().compareTo(valorMax) <= 0)
                .filter(t -> descricaoContem == null || descricaoContem.isBlank()
                        || t.getDescricao().toLowerCase().contains(descricaoContem.toLowerCase()))
                .sorted((a, b) -> b.getData().compareTo(a.getData()))
                .collect(Collectors.toList());
    }

    private BigDecimal somaPorTipo(List<Transacao> transacoes, TipoTransacao tipo) {
        return transacoes.stream()
                .filter(t -> t.getTipo() == tipo)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Garante que o usuário logado é o dono da transação antes de deixar
     * ele ler/editar/excluir.
     */
    private void validarPosse(Transacao transacao) {
        Usuario usuarioLogado = securityUtils.getUsuarioLogado();
        if (transacao.getUsuario() == null || !transacao.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new RecursoNaoEncontradoException("Transação não encontrada com id " + transacao.getId());
        }
    }
}
