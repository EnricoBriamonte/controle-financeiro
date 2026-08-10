package com.portfolio.financas.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

/**
 * Um JWT (JSON Web Token) é um "crachá digital": um texto assinado
 * criptograficamente que prova quem é o usuário sem precisar consultar
 * o banco a cada requisição. Ele tem 3 partes (header.payload.assinatura)
 * e qualquer alteração no conteúdo invalida a assinatura.
 */
@Component
public class JwtUtil {

    // Em produção, defina essa variável de ambiente com um valor longo e aleatório!
    @Value("${JWT_SECRET:chave-secreta-temporaria-trocar-em-producao-0123456789}")
    private String secret;

    // Token expira em 24 horas (em milissegundos)
    private final long expiracaoMs = 1000 * 60 * 60 * 24;

    private SecretKey getChaveAssinatura() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String gerarToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername()) // aqui guardamos o email
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiracaoMs))
                .signWith(getChaveAssinatura(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extrairEmail(String token) {
        return extrairClaim(token, Claims::getSubject);
    }

    public boolean tokenValido(String token, UserDetails userDetails) {
        String email = extrairEmail(token);
        return email.equals(userDetails.getUsername()) && !tokenExpirado(token);
    }

    private boolean tokenExpirado(String token) {
        return extrairClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extrairClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getChaveAssinatura())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolver.apply(claims);
    }
}
