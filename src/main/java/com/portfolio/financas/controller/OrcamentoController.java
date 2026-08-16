package com.portfolio.financas.controller;

import com.portfolio.financas.model.Orcamento;
import com.portfolio.financas.service.OrcamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orcamentos")
public class OrcamentoController {

    @Autowired
    private OrcamentoService orcamentoService;

    @GetMapping
    public List<Orcamento> listar() {
        return orcamentoService.listarTodos();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Orcamento criar(@Valid @RequestBody Orcamento orcamento) {
        return orcamentoService.salvar(orcamento);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        orcamentoService.excluir(id);
    }

    /** Ex: GET /api/orcamentos/comparativo?mes=2026-08 */
    @GetMapping("/comparativo")
    public List<Map<String, Object>> comparativo(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth mes) {
        return orcamentoService.compararComGastoReal(mes);
    }
}
