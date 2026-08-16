package com.portfolio.financas.controller;

import com.portfolio.financas.model.StatusTransacao;
import com.portfolio.financas.model.TipoTransacao;
import com.portfolio.financas.model.Transacao;
import com.portfolio.financas.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    /**
     * Busca com filtros combináveis, todos opcionais.
     * Ex: GET /api/transacoes/buscar?tipo=DESPESA&valorMin=50&descricao=merc
     */
    @GetMapping("/buscar")
    public List<Transacao> buscar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Long contaId,
            @RequestParam(required = false) TipoTransacao tipo,
            @RequestParam(required = false) StatusTransacao status,
            @RequestParam(required = false) BigDecimal valorMin,
            @RequestParam(required = false) BigDecimal valorMax,
            @RequestParam(required = false) String descricao) {
        return transacaoService.buscarComFiltros(inicio, fim, categoriaId, contaId, tipo, status, valorMin, valorMax, descricao);
    }

    /**
     * Exporta as transações do mês em formato CSV, pronto para abrir no Excel.
     * Ex: GET /api/transacoes/relatorios/exportar-csv?mes=2026-08
     */
    @GetMapping("/relatorios/exportar-csv")
    public ResponseEntity<byte[]> exportarCsv(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth mes) {
        byte[] csv = transacaoService.exportarCsv(mes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "transacoes-" + mes + ".csv");

        return new ResponseEntity<>(csv, headers, HttpStatus.OK);
    }
}
