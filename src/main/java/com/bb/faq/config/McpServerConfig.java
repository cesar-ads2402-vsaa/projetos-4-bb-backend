package com.bb.faq.config;

import com.bb.faq.DTOs.TutorialRequestDTO;
import com.bb.faq.DTOs.TutorialResponseDTO;
import com.bb.faq.service.TutorialService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // Deve ser um bean do Spring
public class McpServerConfig {

    private final TutorialService tutorialService;

    // A injeção de dependência acontece aqui
    public McpServerConfig(TutorialService tutorialService) {
        this.tutorialService = tutorialService;
    }

    @Tool(description = "Lista todos os tutoriais de vídeo disponíveis no FAQ do sistema do Banco do Brasil. Retorna IDs, perguntas, categorias e URLs.")
    public List<TutorialResponseDTO> listarTutoriais() {
        return tutorialService.listarTodos();
    }

    @Tool(description = "Cria um novo tutorial de vídeo no sistema de FAQ do Banco do Brasil. Requer os parâmetros: pergunta, youtubeUrl e categoria.")
    public TutorialResponseDTO criarTutorialMcp(TutorialRequestDTO dto) {
        return tutorialService.criarTutorial(dto);
    }
}