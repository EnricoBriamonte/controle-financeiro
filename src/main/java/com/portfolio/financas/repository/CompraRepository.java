package com.portfolio.financas.repository;

import com.portfolio.financas.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Long> {
    List<Compra> findByUsuarioId(Long usuarioId);
    List<Compra> findByCartaoId(Long cartaoId);
}
