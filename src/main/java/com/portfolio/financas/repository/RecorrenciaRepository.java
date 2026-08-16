package com.portfolio.financas.repository;

import com.portfolio.financas.model.Recorrencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecorrenciaRepository extends JpaRepository<Recorrencia, Long> {
    List<Recorrencia> findByUsuarioId(Long usuarioId);
    List<Recorrencia> findByUsuarioIdAndAtivaTrue(Long usuarioId);
}
