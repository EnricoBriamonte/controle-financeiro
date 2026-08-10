package com.portfolio.financas.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * OncePerRequestFilter garante que esse filtro roda exatamente uma vez
 * por requisição. Ele intercepta TODA chamada à API, olha o header
 * "Authorization", e se tiver um token JWT válido, autentica o usuário
 * automaticamente pro resto da requisição.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioDetailsService usuarioDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Sem header ou não é "Bearer <token>": deixa passar sem autenticar
        // (a rota vai ser bloqueada depois pelo SecurityConfig se precisar de login)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); // remove o prefixo "Bearer "
        String email = jwtUtil.extrairEmail(token);

        boolean aindaNaoAutenticado = SecurityContextHolder.getContext().getAuthentication() == null;

        if (email != null && aindaNaoAutenticado) {
            UserDetails usuario = usuarioDetailsService.loadUserByUsername(email);

            if (jwtUtil.tokenValido(token, usuario)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Isso "loga" o usuário pro Spring Security, só para esta requisição
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
