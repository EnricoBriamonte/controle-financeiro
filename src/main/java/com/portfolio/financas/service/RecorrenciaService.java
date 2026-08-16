package com.portfolio.financas.service;

import com.portfolio.financas.exception.RecursoNaoEncontradoException;
import com.portfolio.financas.model.*;
import com.portfolio.financas.repository.RecorrenciaRepository;
import com.portfolio.financas.repository.TransacaoRepository;
import com.portfolio.financas.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecorrenciaService {

    @Autowired
    private RecorrenciaRepository recorrenciaRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private SecurityUtils securityUtils;

    public List<Recorrencia> listarTodas() {
        Usuario usuario = securityUtils.getUsuarioLogado();
        return recorrenciaRepository.findByUsuarioId(usuario.getId());
    }

    public Recorrencia salvar(Recorrencia recorrencia) {
        recorrencia.setUsuario(securityUtils.getUsuarioLogado());
        if (recorrencia.getAtiva() == null) recorrencia.setAtiva(true);
        return recorrenciaRepository.save(recorrencia);
    }

    public void excluir(Long id) {
        Recorrencia recorrencia = buscarPorId(id);
        recorrenciaRepository.delete(recorrencia);
    }

    public Recorrencia buscarPorId(Long id) {
        Recorrencia recorrencia = recorrenciaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Recorrência não encontrada com id " + id));
        validarPosse(recorrencia);
        return recorrencia;
    }

    /**
     * Para cada recorrência ativa do usuário, cria a Transacao correspondente
     * no mês pedido — MAS só se ainda não tiver sido gerada antes (evita
     * duplicar caso o usuário clique duas vezes, por exemplo).
     */
    public List<Transacao> gerarLancamentosDoMes(YearMonth mes) {
        Usuario usuario = securityUtils.getUsuarioLogado();
        List<Recorrencia> ativas = recorrenciaRepository.findByUsuarioIdAndAtivaTrue(usuario.getId());
        LocalDate inicioMes = mes.atDay(1);
        LocalDate fimMes = mes.atEndOfMonth();

        List<Transacao> geradas = new ArrayList<>();

        for (Recorrencia rec : ativas) {
            boolean jaGerado = transacaoRepository
                    .existsByRecorrenciaOrigemIdAndDataBetween(rec.getId(), inicioMes, fimMes);
            if (jaGerado) continue;

            int dia = Math.min(rec.getDiaDoMes(), mes.lengthOfMonth());

            Transacao t = new Transacao();
            t.setDescricao(rec.getDescricao());
            t.setValor(rec.getValor());
            t.setData(mes.atDay(dia));
            t.setTipo(rec.getTipo());
            t.setCategoria(rec.getCategoria());
            t.setNatureza(NaturezaTransacao.FIXA);
            t.setStatus(StatusTransacao.PENDENTE);
            t.setRecorrenciaOrigem(rec);
            t.setUsuario(usuario);

            geradas.add(transacaoRepository.save(t));
        }

        return geradas;
    }

    private void validarPosse(Recorrencia recorrencia) {
        Usuario usuarioLogado = securityUtils.getUsuarioLogado();
        if (recorrencia.getUsuario() == null || !recorrencia.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new RecursoNaoEncontradoException("Recorrência não encontrada com id " + recorrencia.getId());
        }
    }
}
