package com.portfolio.financas.controller;

import com.portfolio.financas.model.Transacao;
import com.portfolio.financas.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @GetMapping
    public List<Transacao> listar() {
        return transacaoService.listarTodas();
    }

    @GetMapping("/{id}")
    public Transacao buscarPorId(@PathVariable Long id) {
        return transacaoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Transacao criar(@Valid @RequestBody Transacao transacao) {
        return transacaoService.salvar(transacao);
    }

    @PutMapping("/{id}")
    public Transacao atualizar(@PathVariable Long id, @Valid @RequestBody Transacao transacao) {
        return transacaoService.atualizar(id, transacao);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        transacaoService.excluir(id);
    }

    /**
     * Ex de chamada: GET /api/transacoes/relatorios/saldo?mes=2026-08
     * @DateTimeFormat ensina o Spring a converter "2026-08" em YearMonth.
     */
    @GetMapping("/relatorios/saldo")
    public Map<String, Object> saldoMensal(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth mes) {
        BigDecimal saldo = transacaoService.calcularSaldoMensal(mes);
        return Map.of("mes", mes.toString(), "saldo", saldo);
    }

    @GetMapping("/relatorios/gastos-por-categoria")
    public Map<String, BigDecimal> gastosPorCategoria(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth mes) {
        return transacaoService.gastosPorCategoria(mes);
    }
}
