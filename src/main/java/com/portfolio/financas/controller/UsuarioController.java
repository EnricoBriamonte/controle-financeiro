package com.portfolio.financas.controller;

import com.portfolio.financas.dto.AtualizarPerfilRequest;
import com.portfolio.financas.dto.TrocarSenhaRequest;
import com.portfolio.financas.model.Usuario;
import com.portfolio.financas.repository.UsuarioRepository;
import com.portfolio.financas.security.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityUtils securityUtils;

    /** Dados do usuário logado. */
    @GetMapping("/me")
    public Usuario perfil() {
        return securityUtils.getUsuarioLogado();
    }

    @PutMapping("/me")
    public Usuario atualizarPerfil(@Valid @RequestBody AtualizarPerfilRequest request) {
        Usuario usuario = securityUtils.getUsuarioLogado();
        usuario.setNome(request.getNome());
        return usuarioRepository.save(usuario);
    }

    @PostMapping("/me/trocar-senha")
    public void trocarSenha(@Valid @RequestBody TrocarSenhaRequest request) {
        Usuario usuario = securityUtils.getUsuarioLogado();

        if (!passwordEncoder.matches(request.getSenhaAtual(), usuario.getSenha())) {
            throw new BadCredentialsException("Senha atual incorreta");
        }

        usuario.setSenha(passwordEncoder.encode(request.getNovaSenha()));
        usuarioRepository.save(usuario);
    }
}
