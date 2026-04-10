package com.bb.faq.controller;

import com.bb.faq.DTOs.TutorialRequestDTO;
import com.bb.faq.DTOs.TutorialResponseDTO;
import com.bb.faq.service.TutorialService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tutoriais")
public class TutorialController {

    private final TutorialService service;

    public TutorialController(TutorialService service) {
        this.service = service;
    }

    @GetMapping
    public List<TutorialResponseDTO> listar() {
        return service.listarTodos();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TutorialResponseDTO criar(@RequestBody TutorialRequestDTO dto) {
        return service.criarTutorial(dto); // 👈 Corrigido o nome do método
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTutorial(@PathVariable Long id) {
        service.deletarTutorial(id); // 👈 Corrigido a sintaxe
        return ResponseEntity.noContent().build();
    }
}