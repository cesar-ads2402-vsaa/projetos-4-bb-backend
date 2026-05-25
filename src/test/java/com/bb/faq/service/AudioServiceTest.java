package com.bb.faq.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.bb.faq.DTOs.AudioResponseDTO;
import com.bb.faq.model.Audio;
import com.bb.faq.model.Tutorial;
import com.bb.faq.model.Usuario;
import com.bb.faq.repository.AudioRepository;
import com.bb.faq.repository.TutorialRepository;
import com.bb.faq.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AudioServiceTest {

    @Mock private AudioRepository audioRepository;
    @Mock private TutorialRepository tutorialRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private BlobContainerClient containerClient;

    @InjectMocks
    private AudioService audioService;

    private Usuario usuarioMock;
    private Tutorial tutorialMock;
    private Audio audioMock;

    @BeforeEach
    void setup() {
        usuarioMock = new Usuario();
        usuarioMock.setEmail("autor@teste.com");
        usuarioMock.setNome("Autor de Teste");

        tutorialMock = new Tutorial();
        tutorialMock.setId(1L);

        audioMock = new Audio();
        audioMock.setId(10L);
        audioMock.setVotos(0);
        audioMock.setTutorial(tutorialMock);
        audioMock.setAutor(usuarioMock);
        audioMock.setCaminhoArquivo("https://azure.com/audio.mp3");
    }

    @Test
    @DisplayName("Deve adicionar um voto ao áudio com sucesso")
    void deveAdicionarVotoComSucesso() {
        when(audioRepository.findById(10L)).thenReturn(Optional.of(audioMock));
        when(audioRepository.save(any(Audio.class))).thenReturn(audioMock);

        AudioResponseDTO resultado = audioService.adicionarVoto(10L);

        assertEquals(1, audioMock.getVotos());
        verify(audioRepository, times(1)).save(audioMock);
    }

    @Test
    @DisplayName("Deve aprovar um áudio")
    void deveAprovarAudio() {
        when(audioRepository.findById(10L)).thenReturn(Optional.of(audioMock));

        audioService.aprovarAudio(10L);

        assertTrue(audioMock.isAprovado());
        verify(audioRepository, times(1)).save(audioMock);
    }

    @Test
    @DisplayName("Deve deletar áudio da Azure e do Banco ao reprovar")
    void deveDeletarAudioDaAzureEDoBanco() {
        String nomeEsperado = "audio.mp3";
        BlobClient blobClientMock = mock(BlobClient.class);

        when(audioRepository.findById(10L)).thenReturn(Optional.of(audioMock));
        when(containerClient.getBlobClient(nomeEsperado)).thenReturn(blobClientMock);

        audioService.reprovarEDeletarAudio(10L);

        verify(blobClientMock, times(1)).delete(); // Garante que chamou a Azure
        verify(audioRepository, times(1)).delete(audioMock); // Garante que deletou do DB
    }

    @Test
    @DisplayName("Deve lançar erro ao votar em áudio inexistente")
    void deveLancarErroAoVotarEmInexistente() {
        when(audioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> audioService.adicionarVoto(99L));
    }


    private void mockSecurityContext(Usuario usuario) {
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(usuario);
    }
}