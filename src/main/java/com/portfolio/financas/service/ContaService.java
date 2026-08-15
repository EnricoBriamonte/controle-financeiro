package com.portfolio.financas.service;

import com.portfolio.financas.exception.RecursoNaoEncontradoException;
import com.portfolio.financas.model.Conta;
import com.portfolio.financas.model.Usuario;
import com.portfolio.financas.repository.ContaRepository;
import com.portfolio.financas.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private SecurityUtils securityUtils;

    public List<Conta> listarTodas() {
        Usuario usuario = securityUtils.getUsuarioLogado();
        return contaRepository.findByUsuarioId(usuario.getId());
    }

    public Conta buscarPorId(Long id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada com id " + id));
        validarPosse(conta);
        return conta;
    }

    public Conta salvar(Conta conta) {
        conta.setUsuario(securityUtils.getUsuarioLogado());
        return contaRepository.save(conta);
    }

    public Conta atualizar(Long id, Conta dadosNovos) {
        Conta existente = buscarPorId(id);
        existente.setNome(dadosNovos.getNome());
        existente.setTipo(dadosNovos.getTipo());
        existente.setSaldoInicial(dadosNovos.getSaldoInicial());
        return contaRepository.save(existente);
    }

    public void excluir(Long id) {
        Conta conta = buscarPorId(id);
        contaRepository.delete(conta);
    }

    private void validarPosse(Conta conta) {
        Usuario usuarioLogado = securityUtils.getUsuarioLogado();
        if (conta.getUsuario() == null || !conta.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new RecursoNaoEncontradoException("Conta não encontrada com id " + conta.getId());
        }
    }
}
