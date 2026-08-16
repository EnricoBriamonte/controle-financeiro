package com.portfolio.financas.controller;

import com.portfolio.financas.model.Meta;
import com.portfolio.financas.service.MetaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/metas")
public class MetaController {

    @Autowired
    private MetaService metaService;

    @GetMapping
    public List<Meta> listar() {
        return metaService.listarTodas();
    }

    @GetMapping("/{id}")
    public Meta buscarPorId(@PathVariable Long id) {
        return metaService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Meta criar(@Valid @RequestBody Meta meta) {
        return metaService.salvar(meta);
    }

    @PutMapping("/{id}")
    public Meta atualizar(@PathVariable Long id, @Valid @RequestBody Meta meta) {
        return metaService.atualizar(id, meta);
    }

    /** Ex: POST /api/metas/3/progresso {"valor": 200} -> soma 200 ao valorAtual */
    @PostMapping("/{id}/progresso")
    public Meta registrarProgresso(@PathVariable Long id, @RequestBody Map<String, BigDecimal> body) {
        return metaService.registrarProgresso(id, body.get("valor"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        metaService.excluir(id);
    }
}
