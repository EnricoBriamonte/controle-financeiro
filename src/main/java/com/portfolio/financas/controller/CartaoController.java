package com.portfolio.financas.controller;

import com.portfolio.financas.model.CartaoCredito;
import com.portfolio.financas.service.CartaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cartoes")
public class CartaoController {

    @Autowired
    private CartaoService cartaoService;

    @GetMapping
    public List<CartaoCredito> listar() {
        return cartaoService.listarTodos();
    }

    @GetMapping("/{id}")
    public CartaoCredito buscarPorId(@PathVariable Long id) {
        return cartaoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartaoCredito criar(@Valid @RequestBody CartaoCredito cartao) {
        return cartaoService.salvar(cartao);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        cartaoService.excluir(id);
    }

    /** Ex: GET /api/cartoes/3/resumo -> {"limite":5000, "utilizado":1250, "disponivel":3750} */
    @GetMapping("/{id}/resumo")
    public Map<String, BigDecimal> resumo(@PathVariable Long id) {
        return cartaoService.resumoUso(id);
    }
}
