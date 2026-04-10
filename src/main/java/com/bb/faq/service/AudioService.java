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
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AudioService {

    private final AudioRepository audioRepository;
    private final TutorialRepository tutorialRepository;
    private final UsuarioRepository usuarioRepository; // <-- NOVO: Injetado para buscar o autor
    private final BlobContainerClient containerClient;



    public AudioService(
            AudioRepository audioRepository,
            TutorialRepository tutorialRepository,
            UsuarioRepository usuarioRepository,
            BlobContainerClient containerClient){

        this.audioRepository = audioRepository;
        this.tutorialRepository = tutorialRepository;
        this.usuarioRepository = usuarioRepository; // <-- NOVO: Inicializado
        this.containerClient = containerClient;
    }


    public AudioResponseDTO salvarAudio(Long tutorialId, String idioma, MultipartFile arquivo) throws IOException {



        Tutorial tutorial = tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new RuntimeException("Tutorial não encontrado!"));

        String contentType = arquivo.getContentType();
        if (contentType == null || !contentType.startsWith("audio/")) {
            throw new IllegalArgumentException("Formato inválido! Apenas ficheiros de áudio são permitidos.");
        }

        String nomeOriginal = arquivo.getOriginalFilename();
        if (nomeOriginal == null || (!nomeOriginal.endsWith(".webm") && !nomeOriginal.endsWith(".mp3") && !nomeOriginal.endsWith(".ogg"))) {
            throw new IllegalArgumentException("Extensão não suportada! Grave em .webm, .mp3 ou .ogg.");
        }


        Usuario usuarioSessao = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Usuario autor = usuarioRepository.findByEmail(usuarioSessao.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado no banco de dados!"));

        String extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
        String nomeArquivoUnico = UUID.randomUUID().toString() + extensao;


        BlobClient blobClient = containerClient.getBlobClient(nomeArquivoUnico);
        blobClient.upload(arquivo.getInputStream(), arquivo.getSize(), true);
        String urlDoAudioNaNuvem = blobClient.getBlobUrl();

        Audio novoAudio = new Audio();
        novoAudio.setCaminhoArquivo(urlDoAudioNaNuvem);
        novoAudio.setTutorial(tutorial);
        novoAudio.setIdioma(idioma);
        novoAudio.setVotos(0);
        novoAudio.setAutor(autor);

        Audio audioSalvo = audioRepository.save(novoAudio);

        return new AudioResponseDTO(
                audioSalvo.getId(),
                audioSalvo.getCaminhoArquivo(),
                audioSalvo.getDataCriacao(),
                audioSalvo.getTutorial().getId(),
                audioSalvo.getVotos(),
                audioSalvo.getIdioma(),
                audioSalvo.getAutor() != null ? audioSalvo.getAutor().getNome() : "Usuário Anônimo"
        );
    }

    @Transactional
    public AudioResponseDTO adicionarVoto(Long audioId) {
        Audio audio = audioRepository.findById(audioId)
                .orElseThrow(() -> new RuntimeException("Áudio não encontrado!"));

        audio.setVotos(audio.getVotos() + 1);
        Audio audioAtualizado = audioRepository.save(audio);

        return new AudioResponseDTO(
                audioAtualizado.getId(),
                audioAtualizado.getCaminhoArquivo(),
                audioAtualizado.getDataCriacao(),
                audioAtualizado.getTutorial().getId(),
                audioAtualizado.getVotos(),
                audioAtualizado.getIdioma(),
                audioAtualizado.getAutor() != null ? audioAtualizado.getAutor().getNome() : "Usuário Anônimo"
        );
    }

    // 🟢 1. PARA A TELA NORMAL DO REACT (Busca só os aprovados)
    public List<AudioResponseDTO> listarAudiosPorTutorialEIdioma(Long tutorialId, String idioma) {
        return audioRepository.findByTutorialIdAndIdiomaAndAprovadoTrueOrderByVotosDesc(tutorialId, idioma)
                .stream()
                .map(audio -> new AudioResponseDTO(
                        audio.getId(),
                        audio.getCaminhoArquivo(),
                        audio.getDataCriacao(),
                        audio.getTutorial().getId(),
                        audio.getVotos(),
                        audio.getIdioma(),
                        audio.getAutor() != null ? audio.getAutor().getNome() : "Usuário Anônimo"
                ))
                .collect(Collectors.toList());
    }

    // 🟢 2. PARA O PAINEL DO ADMIN (Busca só os pendentes)
    public List<AudioResponseDTO> listarAudiosPendentesDeAprovacao() {
        return audioRepository.findByAprovadoFalse()
                .stream()
                .map(audio -> new AudioResponseDTO(
                        audio.getId(),
                        audio.getCaminhoArquivo(),
                        audio.getDataCriacao(),
                        audio.getTutorial().getId(),
                        audio.getVotos(),
                        audio.getIdioma(),
                        audio.getAutor() != null ? audio.getAutor().getNome() : "Usuário Anônimo"
                ))
                .collect(Collectors.toList());
    }


    @Transactional
    public void aprovarAudio(Long audioId) {
        Audio audio = audioRepository.findById(audioId)
                .orElseThrow(() -> new RuntimeException("Áudio não encontrado!"));

        audio.setAprovado(true);
        audioRepository.save(audio);
    }


    @Transactional
    public void reprovarEDeletarAudio(Long audioId) {
        Audio audio = audioRepository.findById(audioId)
                .orElseThrow(() -> new RuntimeException("Áudio não encontrado!"));

        // Aqui você pode adicionar a lógica de deletar da Azure se quiser limpar o Blob!
        // azureBlobService.deletarArquivo(audio.getCaminhoArquivo());

        audioRepository.delete(audio);
    }
}