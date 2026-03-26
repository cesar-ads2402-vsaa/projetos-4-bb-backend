package com.bb.faq.service;

import com.bb.faq.DTOs.TutorialRequestDTO;
import com.bb.faq.DTOs.TutorialResponseDTO;
import com.bb.faq.model.Tutorial;
import com.bb.faq.repository.TutorialRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TutorialService {

    private final TutorialRepository repository;


    public TutorialService(TutorialRepository repository) {
        this.repository = repository;
    }

    // 1. Método para listar todos os tutoriais (GET)
    public List<TutorialResponseDTO> listarTodos() {

        List<Tutorial> tutoriais = repository.findAll();


        return tutoriais.stream()
                .map(tutorial -> new TutorialResponseDTO(
                        tutorial.getId(),
                        tutorial.getPergunta(),
                        tutorial.getYoutubeUrl(),
                        tutorial.getDataCriacao()
                ))
                .collect(Collectors.toList());
    }

    // 2. Método para criar um novo tutorial (POST)
    public TutorialResponseDTO criar(TutorialRequestDTO dto) {

        // Passo A: Transformar o DTO (que veio do Next.js) numa Entidade vazia
        Tutorial novoTutorial = new Tutorial();
        novoTutorial.setPergunta(dto.pergunta());
        novoTutorial.setYoutubeUrl(dto.youtubeUrl());
        // (O ID e a data de criação são gerados automaticamente, não precisamos de os colocar aqui)

        // Passo B: Mandar o Repository guardar na base de dados (SQLite)
        Tutorial tutorialGuardado = repository.save(novoTutorial);

        // Passo C: Transformar a Entidade guardada (agora já com ID) num ResponseDTO para devolver ao Next.js
        return new TutorialResponseDTO(
                tutorialGuardado.getId(),
                tutorialGuardado.getPergunta(),
                tutorialGuardado.getYoutubeUrl(),
                tutorialGuardado.getDataCriacao()
        );
    }
}
