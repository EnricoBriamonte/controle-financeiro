package com.portfolio.financas.controller;

import com.portfolio.financas.model.Recorrencia;
import com.portfolio.financas.model.Transacao;
import com.portfolio.financas.service.RecorrenciaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/recorrencias")
public class RecorrenciaController {

    @Autowired
    private RecorrenciaService recorrenciaService;

    @GetMapping
    public List<Recorrencia> listar() {
        return recorrenciaService.listarTodas();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Recorrencia criar(@Valid @RequestBody Recorrencia recorrencia) {
        return recorrenciaService.salvar(recorrencia);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        recorrenciaService.excluir(id);
    }

    /**
     * Gera os lançamentos do mês pedido a partir de todas as recorrências
     * ativas. Ex: POST /api/recorrencias/gerar?mes=2026-08
     */
    @PostMapping("/gerar")
    public List<Transacao> gerar(@RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth mes) {
        return recorrenciaService.gerarLancamentosDoMes(mes);
    }
}