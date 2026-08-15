package com.portfolio.financas.repository;

import com.portfolio.financas.model.CartaoCredito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartaoRepository extends JpaRepository<CartaoCredito, Long> {
    List<CartaoCredito> findByUsuarioId(Long usuarioId);
}
