package com.bb.faq.mcp;

import com.bb.faq.DTOs.TutorialRequestDTO;
import com.bb.faq.DTOs.TutorialResponseDTO;
import com.bb.faq.service.TutorialService;
import com.bb.faq.service.UsuarioService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FaqMcpTools {

    private final TutorialService tutorialService;
    private final UsuarioService usuarioService;

    public FaqMcpTools(TutorialService tutorialService, UsuarioService usuarioService) {
        this.tutorialService = tutorialService;
        this.usuarioService = usuarioService;
    }

    @Tool(description = "Lista todos os tutoriais de vídeo disponíveis no FAQ do sistema. Retorna os IDs, perguntas, categorias e URLs do YouTube.")
    public List<TutorialResponseDTO> listarTutoriais() {
        return tutorialService.listarTodos();
    }

    @Tool(description = "Cria um novo tutorial de vídeo no sistema do FAQ. Exige a pergunta, a URL do YouTube e a categoria.")
    public TutorialResponseDTO criarTutorialMcp(String pergunta, String youtubeUrl, String categoria) {
        TutorialRequestDTO dto = new TutorialRequestDTO(pergunta, youtubeUrl, categoria);
        return tutorialService.criarTutorial(dto);
    }
}