package com.portfolio.financas.security;

import com.portfolio.financas.model.Usuario;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Depois que o JwtAuthFilter autentica a requisição, o usuário fica
 * disponível no SecurityContextHolder durante toda a requisição.
 * Essa classe só facilita o acesso a ele a partir de qualquer Service.
 */
@Component
public class SecurityUtils {

    public Usuario getUsuarioLogado() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
