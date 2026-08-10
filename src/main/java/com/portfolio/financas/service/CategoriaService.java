package com.portfolio.financas.service;

import com.portfolio.financas.exception.RecursoNaoEncontradoException;
import com.portfolio.financas.model.Categoria;
import com.portfolio.financas.model.Usuario;
import com.portfolio.financas.repository.CategoriaRepository;
import com.portfolio.financas.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private SecurityUtils securityUtils;

    public List<Categoria> listarTodas() {
        Usuario usuario = securityUtils.getUsuarioLogado();
        return categoriaRepository.findByUsuarioId(usuario.getId());
    }

    public Categoria buscarPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada com id " + id));
        validarPosse(categoria);
        return categoria;
    }

    public Categoria salvar(Categoria categoria) {
        categoria.setUsuario(securityUtils.getUsuarioLogado());
        return categoriaRepository.save(categoria);
    }

    public Categoria atualizar(Long id, Categoria dadosNovos) {
        Categoria categoriaExistente = buscarPorId(id); // já valida posse
        categoriaExistente.setNome(dadosNovos.getNome());
        categoriaExistente.setDescricao(dadosNovos.getDescricao());
        return categoriaRepository.save(categoriaExistente);
    }

    public void excluir(Long id) {
        Categoria categoria = buscarPorId(id); // já valida posse
        categoriaRepository.delete(categoria);
    }

    /**
     * Garante que o usuário logado é o dono da categoria antes de deixar
     * ele ler/editar/excluir. Sem isso, qualquer usuário autenticado
     * poderia acessar dados de qualquer outro só sabendo o ID.
     */
    private void validarPosse(Categoria categoria) {
        Usuario usuarioLogado = securityUtils.getUsuarioLogado();
        if (categoria.getUsuario() == null || !categoria.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new RecursoNaoEncontradoException("Categoria não encontrada com id " + categoria.getId());
        }
    }
}
