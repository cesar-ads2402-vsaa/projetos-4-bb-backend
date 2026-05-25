package com.bb.faq.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.bb.faq.DTOs.TutorialRequestDTO;
import com.bb.faq.DTOs.TutorialResponseDTO;
import com.bb.faq.model.Audio;
import com.bb.faq.model.Tutorial;
import com.bb.faq.repository.TutorialRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TutorialServiceTest {

    @Mock
    private TutorialRepository tutorialRepository;

    @Mock
    private BlobContainerClient containerClient;

    @InjectMocks
    private TutorialService tutorialService;

    @Test
    void deveListarTodosOsTutoriaisComSucesso() {
        Tutorial tutorialMock = new Tutorial();
        tutorialMock.setId(1L);
        tutorialMock.setPergunta("Como acessar o contracheque?");
        tutorialMock.setYoutubeUrl("https://youtube.com/exemplo");
        tutorialMock.setDataCriacao(LocalDateTime.now());

        when(tutorialRepository.findAll()).thenReturn(List.of(tutorialMock));

        List<TutorialResponseDTO> resultado = tutorialService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).id());
        assertEquals("Como acessar o contracheque?", resultado.get(0).pergunta());
        assertEquals("https://youtube.com/exemplo", resultado.get(0).youtubeUrl());
    }

    @Test
    void deveCriarTutorialComSucesso() {
        TutorialRequestDTO requestDTO = new TutorialRequestDTO(
                "Como alterar a senha do sistema?",
                "https://youtube.com/senha"
        );

        Tutorial tutorialSalvo = new Tutorial();
        tutorialSalvo.setId(2L);
        tutorialSalvo.setPergunta(requestDTO.pergunta());
        tutorialSalvo.setYoutubeUrl(requestDTO.youtubeUrl());
        tutorialSalvo.setDataCriacao(LocalDateTime.now());

        when(tutorialRepository.save(any(Tutorial.class))).thenReturn(tutorialSalvo);

        TutorialResponseDTO resultado = tutorialService.criarTutorial(requestDTO);

        assertNotNull(resultado);
        assertEquals(2L, resultado.id());
        assertEquals("Como alterar a senha do sistema?", resultado.pergunta());
        verify(tutorialRepository, times(1)).save(any(Tutorial.class));
    }

    @Test
    void deveDeletarTutorialEApagarAudiosAssociadosNaAzure() {
        Long tutorialId = 1L;

        Tutorial tutorialMock = new Tutorial();
        tutorialMock.setId(tutorialId);

        Audio audioMock = new Audio();
        audioMock.setCaminhoArquivo("https://minha-azure.com/meu-container/audio-teste-123.mp3");
        tutorialMock.setAudios(List.of(audioMock));

        BlobClient blobClientFalso = mock(BlobClient.class);

        when(tutorialRepository.findById(tutorialId)).thenReturn(Optional.of(tutorialMock));

        when(containerClient.getBlobClient("audio-teste-123.mp3")).thenReturn(blobClientFalso);

        tutorialService.deletarTutorial(tutorialId);

        verify(blobClientFalso, times(1)).deleteIfExists();

        verify(tutorialRepository, times(1)).delete(tutorialMock);
    }

    @Test
    void deveLancarExcecaoAoTentarDeletarTutorialInexistente() {
        Long tutorialIdInvalido = 99L;
        when(tutorialRepository.findById(tutorialIdInvalido)).thenReturn(Optional.empty());

        RuntimeException erro = assertThrows(RuntimeException.class, () -> {
            tutorialService.deletarTutorial(tutorialIdInvalido);
        });

        assertEquals("Tutorial não encontrado!", erro.getMessage());
        
        verify(tutorialRepository, never()).delete(any());
    }
}