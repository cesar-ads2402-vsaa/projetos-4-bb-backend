package com.bb.faq.controller;

import com.bb.faq.DTOs.LoginDTO;
import com.bb.faq.DTOs.RegistroDTO;
import com.bb.faq.DTOs.TokenResponseDTO;
import com.bb.faq.DTOs.UsuarioResponseDTO;
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

    public UsuarioController(UsuarioService service) {
        this.service = service;
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
}