package com.portfolio.financas.controller;

import com.portfolio.financas.model.Compra;
import com.portfolio.financas.service.CompraService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    @Autowired
    private CompraService compraService;

    @GetMapping
    public List<Compra> listar() {
        return compraService.listarTodas();
    }

    @GetMapping("/{id}")
    public Compra buscarPorId(@PathVariable Long id) {
        return compraService.buscarPorId(id);
    }

    /**
     * Ao criar uma compra, as parcelas são geradas automaticamente
     * pelo CompraService — o cliente só manda descrição, valor total,
     * data, número de parcelas e o id do cartão.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Compra criar(@Valid @RequestBody Compra compra) {
        return compraService.salvar(compra);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        compraService.excluir(id);
    }
}
