package com.bb.faq.controller;

import com.bb.faq.DTOs.IdiomaRequestDTO;
import com.bb.faq.DTOs.IdiomaResponseDTO;
import com.bb.faq.service.IdiomaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdiomaControllerTest {

    @Mock
    private IdiomaService service;

    @InjectMocks
    private IdiomaController controller;

    private IdiomaRequestDTO idiomaRequest;
    private IdiomaResponseDTO idiomaResponse;

    @BeforeEach
    void setup() {
        idiomaRequest = new IdiomaRequestDTO("Português", "pt-BR");
        idiomaResponse = new IdiomaResponseDTO(1L, "Português", "pt-BR");
    }

    @Test
    @DisplayName("Deve listar todos os idiomas com status 200")
    void deveListarIdiomas() {
        when(service.listarTodos()).thenReturn(List.of(idiomaResponse));

        ResponseEntity<List<IdiomaResponseDTO>> resposta = controller.listarIdiomas();

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(1, resposta.getBody().size());
        assertEquals("pt-BR", resposta.getBody().getFirst().codigo());
        verify(service, times(1)).listarTodos();
    }

    @Test
    @DisplayName("Deve criar idioma e retornar DTO com status 200")
    void deveCriarIdioma() {
        when(service.criar(idiomaRequest)).thenReturn(idiomaResponse);

        ResponseEntity<IdiomaResponseDTO> resposta = controller.criarIdioma(idiomaRequest);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("Português", resposta.getBody().nome());
        verify(service, times(1)).criar(idiomaRequest);
    }

    @Test
    @DisplayName("Deve deletar idioma e retornar status 204")
    void deveDeletarIdioma() {
        ResponseEntity<Void> resposta = controller.deletarIdioma(1L);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        verify(service, times(1)).deletar(1L);
    }
}
