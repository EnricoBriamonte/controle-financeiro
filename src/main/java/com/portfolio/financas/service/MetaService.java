package com.portfolio.financas.service;

import com.portfolio.financas.exception.RecursoNaoEncontradoException;
import com.portfolio.financas.model.Meta;
import com.portfolio.financas.model.Usuario;
import com.portfolio.financas.repository.MetaRepository;
import com.portfolio.financas.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MetaService {

    @Autowired
    private MetaRepository metaRepository;

    @Autowired
    private SecurityUtils securityUtils;

    public List<Meta> listarTodas() {
        Usuario usuario = securityUtils.getUsuarioLogado();
        return metaRepository.findByUsuarioId(usuario.getId());
    }

    public Meta buscarPorId(Long id) {
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Meta não encontrada com id " + id));
        validarPosse(meta);
        return meta;
    }

    public Meta salvar(Meta meta) {
        meta.setUsuario(securityUtils.getUsuarioLogado());
        if (meta.getValorAtual() == null) meta.setValorAtual(BigDecimal.ZERO);
        return metaRepository.save(meta);
    }

    public Meta atualizar(Long id, Meta dadosNovos) {
        Meta existente = buscarPorId(id);
        existente.setNome(dadosNovos.getNome());
        existente.setValorMeta(dadosNovos.getValorMeta());
        existente.setPrazo(dadosNovos.getPrazo());
        return metaRepository.save(existente);
    }

    /**
     * Registra um "depósito" na meta, somando ao valor já acumulado —
     * é como o usuário vai marcando progresso (ex: "guardei mais R$ 200").
     */
    public Meta registrarProgresso(Long id, BigDecimal valorAdicional) {
        Meta meta = buscarPorId(id);
        meta.setValorAtual(meta.getValorAtual().add(valorAdicional));
        return metaRepository.save(meta);
    }

    public void excluir(Long id) {
        Meta meta = buscarPorId(id);
        metaRepository.delete(meta);
    }

    private void validarPosse(Meta meta) {
        Usuario usuarioLogado = securityUtils.getUsuarioLogado();
        if (meta.getUsuario() == null || !meta.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new RecursoNaoEncontradoException("Meta não encontrada com id " + meta.getId());
        }
    }
}
