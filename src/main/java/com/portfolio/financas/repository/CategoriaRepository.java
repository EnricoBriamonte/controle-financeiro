package com.portfolio.financas.repository;

import com.portfolio.financas.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * O Spring Data JPA gera a implementação disso automaticamente.
 * Só de estender JpaRepository, já ganhamos métodos como
 * save(), findById(), findAll(), deleteById(), etc. de graça.
 */
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByUsuarioId(Long usuarioId);
}
