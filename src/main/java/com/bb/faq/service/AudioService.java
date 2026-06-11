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
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AudioService {

    private final AudioRepository audioRepository;
    private final TutorialRepository tutorialRepository;
    private final UsuarioRepository usuarioRepository;
    private final BlobContainerClient containerClient;



    public AudioService(
            AudioRepository audioRepository,
            TutorialRepository tutorialRepository,
            UsuarioRepository usuarioRepository,
            BlobContainerClient containerClient){

        this.audioRepository = audioRepository;
        this.tutorialRepository = tutorialRepository;
        this.usuarioRepository = usuarioRepository;
        this.containerClient = containerClient;
    }



    public AudioResponseDTO salvarAudio(Long tutorialId, String idioma, MultipartFile arquivo) throws IOException {

        // Validações rápidas em banco de dados
        Tutorial tutorial = tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new RuntimeException("Tutorial não encontrado!"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuario = authentication.getName();
        Usuario autor = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        // Cria o registro no banco com uma string temporária de processamento
        Audio novoAudio = new Audio();
        novoAudio.setCaminhoArquivo("PROCESSANDO_EM_BACKGROUND...");
        novoAudio.setTutorial(tutorial);
        novoAudio.setIdioma(idioma);
        novoAudio.setAutor(autor);
        novoAudio.setAprovado(false);

        Audio audioSalvo = audioRepository.save(novoAudio);

        // DISPARO ASSÍNCRONO: Cria uma nova Thread e delega o arquivo e o ID gerado
        processarEEnviarAudioAssincrono(arquivo, audioSalvo.getId());

        // Retorna a resposta imediatamente para o Frontend
        return new AudioResponseDTO(
                audioSalvo.getId(),
                audioSalvo.getCaminhoArquivo(),
                audioSalvo.getDataCriacao(),
                tutorial.getId(),
                audioSalvo.getVotos(),
                audioSalvo.getIdioma(),
                autor.getNome()
        );
    }

    @Async
    public void processarEEnviarAudioAssincrono(MultipartFile arquivo, Long audioId) {
        File tempInput = null;
        File tempOutput = null;

        try {
            tempInput = File.createTempFile("upload-", "-original");
            tempOutput = File.createTempFile("convertido-", ".mp3");
            arquivo.transferTo(tempInput);

            AudioAttributes audioAttrs = new AudioAttributes();
            audioAttrs.setCodec("libmp3lame");
            audioAttrs.setBitRate(128000);
            audioAttrs.setChannels(2);
            audioAttrs.setSamplingRate(44100);

            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setOutputFormat("mp3");
            attrs.setAudioAttributes(audioAttrs);

            Encoder encoder = new Encoder();
            encoder.encode(new MultimediaObject(tempInput), tempOutput, attrs);

            String nomeArquivoFinal = UUID.randomUUID().toString() + ".mp3";
            BlobClient blobClient = containerClient.getBlobClient(nomeArquivoFinal);

            try (FileInputStream fis = new FileInputStream(tempOutput)) {
                blobClient.upload(fis, tempOutput.length(), true);
            }

            String urlFinalAzure = blobClient.getBlobUrl();

            Audio audioParaAtualizar = audioRepository.findById(audioId)
                    .orElseThrow(() -> new RuntimeException("Falha crítica: Áudio sumiu do banco durante processamento."));

            audioParaAtualizar.setCaminhoArquivo(urlFinalAzure);
            audioRepository.save(audioParaAtualizar);

        } catch (Exception e) {
            System.err.println("Erro crítico na Thread [" + Thread.currentThread().getName() + "] ao processar áudio ID " + audioId + ": " + e.getMessage());

            audioRepository.findById(audioId).ifPresent(audioErro -> {
                audioErro.setCaminhoArquivo("ERRO_AO_PROCESSAR_AUDIO");
                audioRepository.save(audioErro);
            });
        } finally {
            if (tempInput != null && tempInput.exists()) tempInput.delete();
            if (tempOutput != null && tempOutput.exists()) tempOutput.delete();
        }
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

    // PARA A TELA NORMAL DO REACT
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

    // 2. PARA O PAINEL DO ADMIN
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

        String url = audio.getCaminhoArquivo();
        String nomeArquivo = url.substring(url.lastIndexOf("/") + 1);

        try {
            containerClient.getBlobClient(nomeArquivo).delete();
        } catch (Exception e) {
            System.err.println("Aviso: Arquivo não encontrado no Azure, prosseguindo com delete no banco.");
        }

        audioRepository.delete(audio);
    }
    public List<AudioResponseDTO> listarTodosAprovados() {
        return audioRepository.findAll().stream()
                .filter(Audio::isAprovado)
                .map(audio -> new AudioResponseDTO(
                        audio.getId(),
                        audio.getCaminhoArquivo(),
                        audio.getDataCriacao(),
                        audio.getTutorial().getId(),
                        audio.getVotos(),
                        audio.getIdioma(),
                        audio.getAutor() != null ? audio.getAutor().getNome() : "Usuário"
                )).collect(Collectors.toList());
    }
}