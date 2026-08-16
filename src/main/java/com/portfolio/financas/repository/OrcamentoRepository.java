package com.portfolio.financas.repository;

import com.portfolio.financas.model.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {
    List<Orcamento> findByUsuarioId(Long usuarioId);
    List<Orcamento> findByUsuarioIdAndMes(Long usuarioId, String mes);
}
