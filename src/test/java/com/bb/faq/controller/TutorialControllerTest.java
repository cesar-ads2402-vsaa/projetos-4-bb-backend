package com.bb.faq.controller;

import com.bb.faq.DTOs.TutorialRequestDTO;
import com.bb.faq.DTOs.TutorialResponseDTO;
import com.bb.faq.service.TutorialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TutorialControllerTest {

    @Mock
    private TutorialService service;

    @InjectMocks
    private TutorialController controller;

    private TutorialResponseDTO tutorialResponse;
    private TutorialRequestDTO tutorialRequest;

    @BeforeEach
    void setup() {
        tutorialRequest = new TutorialRequestDTO("Como usar o app?", "https://youtube.com/watch?v=abc");
        tutorialResponse = new TutorialResponseDTO(
                1L,
                "Como usar o app?",
                "https://youtube.com/watch?v=abc",
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Deve listar todos os tutoriais")
    void deveListarTutoriais() {
        when(service.listarTodos()).thenReturn(List.of(tutorialResponse));

        List<TutorialResponseDTO> resultado = controller.listar();

        assertEquals(1, resultado.size());
        assertEquals("Como usar o app?", resultado.getFirst().pergunta());
        verify(service, times(1)).listarTodos();
    }

    @Test
    @DisplayName("Deve criar um tutorial e retornar DTO")
    void deveCriarTutorial() {
        when(service.criarTutorial(tutorialRequest)).thenReturn(tutorialResponse);

        TutorialResponseDTO resultado = controller.criar(tutorialRequest);

        assertEquals(1L, resultado.id());
        assertEquals(tutorialRequest.pergunta(), resultado.pergunta());
        verify(service, times(1)).criarTutorial(tutorialRequest);
    }

    @Test
    @DisplayName("Deve deletar tutorial e retornar status 204")
    void deveDeletarTutorial() {
        ResponseEntity<Void> resposta = controller.deletarTutorial(1L);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        verify(service, times(1)).deletarTutorial(1L);
    }
}
