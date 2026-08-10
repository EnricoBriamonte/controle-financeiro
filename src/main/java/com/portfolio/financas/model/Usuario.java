package com.portfolio.financas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Representa um usuário do sistema. Implementar UserDetails é o "contrato"
 * que o Spring Security exige pra saber como autenticar essa entidade
 * (quais são as credenciais, se a conta está ativa, etc).
 *
 * @JsonIgnoreProperties esconde os métodos herdados de UserDetails
 * (getAuthorities, isEnabled, etc) na serialização JSON — eles são
 * detalhes internos de autenticação, não dados que o cliente precisa ver.
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"authorities", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled", "username", "password"})
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Aqui fica o HASH da senha (nunca a senha em texto puro).
     * O BCryptPasswordEncoder cuida de gerar e comparar esse hash.
     *
     * WRITE_ONLY: o cliente pode ENVIAR a senha (no cadastro), mas ela
     * nunca é devolvida nas respostas da API — nem o hash deve vazar.
     */
    @NotBlank(message = "A senha é obrigatória")
    @com.fasterxml.jackson.annotation.JsonProperty(access = com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY)
    private String senha;

    // --- Métodos exigidos pela interface UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
