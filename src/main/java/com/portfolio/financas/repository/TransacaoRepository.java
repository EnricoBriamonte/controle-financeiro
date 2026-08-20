package com.portfolio.financas.repository;

import com.portfolio.financas.model.TipoTransacao;
import com.portfolio.financas.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByDataBetween(LocalDate inicio, LocalDate fim);

    List<Transacao> findByTipo(TipoTransacao tipo);

    List<Transacao> findByCategoriaId(Long categoriaId);

    List<Transacao> findByUsuarioId(Long usuarioId);

    List<Transacao> findByUsuarioIdAndDataBetween(Long usuarioId, LocalDate inicio, LocalDate fim);

    boolean existsByRecorrenciaOrigemIdAndDataBetween(Long recorrenciaId, LocalDate inicio, LocalDate fim);
}
