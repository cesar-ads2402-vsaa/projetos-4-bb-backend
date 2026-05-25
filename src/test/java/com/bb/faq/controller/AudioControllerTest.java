package com.bb.faq.controller;

import com.bb.faq.DTOs.AudioResponseDTO;
import com.bb.faq.service.AudioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AudioControllerTest {

    @Mock
    private AudioService audioService;

    @InjectMocks
    private AudioController controller;

    private AudioResponseDTO audioResponse;

    @BeforeEach
    void setup() {
        audioResponse = new AudioResponseDTO(
                10L,
                "https://azure.com/audio.mp3",
                LocalDateTime.now(),
                1L,
                5,
                "pt-BR",
                "Autor Teste"
        );
    }

    @Test
    @DisplayName("Deve fazer upload de áudio e retornar DTO")
    void deveFazerUploadAudio() throws IOException {
        MultipartFile arquivo = mock(MultipartFile.class);
        when(audioService.salvarAudio(1L, "pt-BR", arquivo)).thenReturn(audioResponse);

        AudioResponseDTO resultado = controller.fazerUploadAudio(1L, "pt-BR", arquivo);

        assertEquals(audioResponse, resultado);
        verify(audioService, times(1)).salvarAudio(1L, "pt-BR", arquivo);
    }

    @Test
    @DisplayName("Deve votar no áudio e retornar DTO atualizado")
    void deveVotarNoAudio() {
        when(audioService.adicionarVoto(10L)).thenReturn(audioResponse);

        AudioResponseDTO resultado = controller.votarNoAudio(10L);

        assertEquals(5, resultado.votos());
        verify(audioService, times(1)).adicionarVoto(10L);
    }

    @Test
    @DisplayName("Deve buscar áudios ordenados por tutorial e idioma")
    void deveBuscarAudiosOrdenados() {
        when(audioService.listarAudiosPorTutorialEIdioma(1L, "pt-BR"))
                .thenReturn(List.of(audioResponse));

        List<AudioResponseDTO> resultado = controller.buscarAudiosOrdenados(1L, "pt-BR");

        assertEquals(1, resultado.size());
        assertEquals("pt-BR", resultado.getFirst().idioma());
        verify(audioService, times(1)).listarAudiosPorTutorialEIdioma(1L, "pt-BR");
    }

    @Test
    @DisplayName("Deve deletar áudio e retornar status 204")
    void deveDeletarAudio() {
        ResponseEntity<Void> resposta = controller.deletarAudio(10L);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        verify(audioService, times(1)).reprovarEDeletarAudio(10L);
    }

    @Test
    @DisplayName("Deve listar áudios pendentes de moderação")
    void deveListarPendentes() {
        when(audioService.listarAudiosPendentesDeAprovacao()).thenReturn(List.of(audioResponse));

        List<AudioResponseDTO> resultado = controller.listarPendentes();

        assertFalse(resultado.isEmpty());
        verify(audioService, times(1)).listarAudiosPendentesDeAprovacao();
    }

    @Test
    @DisplayName("Deve aprovar áudio e retornar status 200")
    void deveAprovarAudio() {
        ResponseEntity<Void> resposta = controller.aprovarAudio(10L);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(audioService, times(1)).aprovarAudio(10L);
    }

    @Test
    @DisplayName("Deve listar todos os áudios aprovados")
    void deveListarAprovados() {
        when(audioService.listarTodosAprovados()).thenReturn(List.of(audioResponse));

        List<AudioResponseDTO> resultado = controller.listarAprovados();

        assertEquals(1, resultado.size());
        verify(audioService, times(1)).listarTodosAprovados();
    }

    @Test
    @DisplayName("Deve reprovar áudio e retornar status 204")
    void deveReprovarAudio() {
        ResponseEntity<Void> resposta = controller.reprovarAudio(10L);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        verify(audioService, times(1)).reprovarEDeletarAudio(10L);
    }
}
