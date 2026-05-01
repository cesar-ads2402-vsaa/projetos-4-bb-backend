package com.bb.faq.controller;

import com.bb.faq.DTOs.IdiomaRequestDTO;
import com.bb.faq.DTOs.IdiomaResponseDTO;
import com.bb.faq.service.IdiomaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/idiomas")
public class IdiomaController {

    @Autowired
    private IdiomaService service;

    @GetMapping
    public ResponseEntity<List<IdiomaResponseDTO>> listarIdiomas() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @PostMapping
    public ResponseEntity<IdiomaResponseDTO> criarIdioma(@RequestBody IdiomaRequestDTO dto) {
        return ResponseEntity.ok(service.criar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarIdioma(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}