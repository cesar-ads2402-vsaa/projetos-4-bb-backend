package com.bb.faq.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.bb.faq.DTOs.AudioResponseDTO;
import com.bb.faq.model.Audio;
import com.bb.faq.model.Tutorial;
import com.bb.faq.repository.AudioRepository;
import com.bb.faq.repository.TutorialRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
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
    private final BlobContainerClient containerClient;


    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Value("${azure.storage.container-name}")
    private String containerName;

    public AudioService(
            AudioRepository audioRepository,
            TutorialRepository tutorialRepository,
            @Value("${azure.storage.connection-string}") String connectionString,
            @Value("${azure.storage.container-name}") String containerName) {

        this.audioRepository = audioRepository;
        this.tutorialRepository = tutorialRepository;

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
        this.containerClient = blobServiceClient.getBlobContainerClient(containerName);
    }

    public AudioResponseDTO salvarAudio(Long tutorialId, MultipartFile arquivo) throws IOException {
        //-------------Configs de Seguranca-----------------//

        // 1. Verifica se o video (Tutorial) existe no banco de dados
        Tutorial tutorial = tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new RuntimeException("Tutorial não encontrado!"));

        // 2. Verifica o tipo do arquivo
        String contentType = arquivo.getContentType();
        if (contentType == null || !contentType.startsWith("audio/")) {
            throw new IllegalArgumentException("Formato inválido! Apenas ficheiros de áudio são permitidos.");
        }

        // 2.1 Verifica pela extensao do arquivo
        String nomeOriginal = arquivo.getOriginalFilename();
        if (nomeOriginal == null || (!nomeOriginal.endsWith(".webm") && !nomeOriginal.endsWith(".mp3") && !nomeOriginal.endsWith(".ogg"))) {
            throw new IllegalArgumentException("Extensão não suportada! Grave em .webm, .mp3 ou .ogg.");
        }

        // 3. Gera o nome único para o arquivo
        String extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
        String nomeArquivoUnico = UUID.randomUUID().toString() + extensao;

        //---------------------------------------------------//


        // 4. Faz o upload do arquivo direto para a nuvem
        BlobClient blobClient = containerClient.getBlobClient(nomeArquivoUnico);
        blobClient.upload(arquivo.getInputStream(), arquivo.getSize(), true);

        // 5. Pega o link publico que a Azure gerou
        String urlDoAudioNaNuvem = blobClient.getBlobUrl();

        // 6. Salva no banco de dados
        Audio novoAudio = new Audio();
        novoAudio.setCaminhoArquivo(urlDoAudioNaNuvem);
        novoAudio.setTutorial(tutorial);
        Audio audioSalvo = audioRepository.save(novoAudio);

        //------------------------------------------------//

        // 6. Retorna o DTO
        return new AudioResponseDTO(
                audioSalvo.getId(),
                audioSalvo.getCaminhoArquivo(),
                audioSalvo.getDataCriacao(),
                audioSalvo.getTutorial().getId(),
                audioSalvo.getVotos()
        );
    }



    @Transactional
    public AudioResponseDTO adicionarVoto(Long audioId) {

        // 1. Procura o áudio na base de dados
        Audio audio = audioRepository.findById(audioId)
                .orElseThrow(() -> new RuntimeException("Áudio não encontrado!"));

        // 2. Soma +1 ao número de votos atual
        audio.setVotos(audio.getVotos() + 1);

        // 3. Guarda a alteração
        Audio audioAtualizado = audioRepository.save(audio);

        // 4. Devolve o DTO atualizado para o Frontend mostrar o novo numero
        return new AudioResponseDTO(
                audioAtualizado.getId(),
                audioAtualizado.getCaminhoArquivo(),
                audioAtualizado.getDataCriacao(),
                audioAtualizado.getTutorial().getId(),
                audioAtualizado.getVotos()
        );
    }
    public List<AudioResponseDTO> listarAudiosDoTutorial(Long tutorialId) {
        // Orderna os Audios pelo mais votado
        List<Audio> audios = audioRepository.findByTutorialIdOrderByVotosDesc(tutorialId);

        return audios.stream()
                .map(audio -> new AudioResponseDTO(
                        audio.getId(),
                        audio.getCaminhoArquivo(),
                        audio.getDataCriacao(),
                        audio.getTutorial().getId(),
                        audio.getVotos()
                ))
                .collect(Collectors.toList());
    }
}