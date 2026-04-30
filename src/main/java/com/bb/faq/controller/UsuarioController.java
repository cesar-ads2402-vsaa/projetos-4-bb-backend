package com.bb.faq.controller;

import com.bb.faq.DTOs.*;
import com.bb.faq.service.PasswordResetService;
import com.bb.faq.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;
    private final PasswordResetService password;

    public UsuarioController(UsuarioService service, PasswordResetService password) {
        this.service = service;
        this.password = password;
    }

    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    public void registrar(@RequestBody RegistroDTO dto) {
        service.registrar(dto);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponseDTO login(@RequestBody LoginDTO dto) {
        return service.login(dto);
    }

    @PatchMapping("/{id}/promover")
    public ResponseEntity<Void> promoverUsuario(@PathVariable Long id) {
        service.promoverParaAdmin(id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}/rebaixar")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> rebaixarUsuario(@PathVariable Long id) {
        service.rebaixarOuDeletarAdmin(id);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @GetMapping("/comuns")
    public List<UsuarioResponseDTO> buscarUsuarios() {
        return service.listarUsuariosComuns();
    }

    @PutMapping("/trocar-senha")
    public ResponseEntity<String> trocarSenha(@RequestBody TrocarSenhaDTO dto) {
        service.trocarSenha(dto);
        return ResponseEntity.ok("Senha alterada com sucesso!");
    }

    @PostMapping("/esqueci-senha")
    public ResponseEntity<String> solicitarRecuperacao(@RequestBody EsqueciSenhaDTO dto) {
        password.solicitarRecuperacaoSenha(dto.email());
        return ResponseEntity.ok("Se o e-mail estiver cadastrado, você receberá um link de recuperação em instantes.");
    }

    @PostMapping("/resetar-senha")
    public ResponseEntity<String> resetarSenha(@RequestBody ResetarSenhaDTO dto) {
        password.resetarSenha(dto.token(), dto.novaSenha());
        return ResponseEntity.ok("Sua senha foi alterada com sucesso! Você já pode fazer login.");
    }
}