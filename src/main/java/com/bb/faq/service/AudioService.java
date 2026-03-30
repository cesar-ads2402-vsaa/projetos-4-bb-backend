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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AudioService {

    private final AudioRepository audioRepository;
    private final TutorialRepository tutorialRepository;


    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Value("${azure.storage.container-name}")
    private String containerName;

    public AudioService(AudioRepository audioRepository, TutorialRepository tutorialRepository) {
        this.audioRepository = audioRepository;
        this.tutorialRepository = tutorialRepository;
    }

    public AudioResponseDTO salvarAudio(Long tutorialId, MultipartFile arquivo) throws IOException {

        // 1. Verifica se o video (Tutorial) existe no banco de dados
        Tutorial tutorial = tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new RuntimeException("Tutorial não encontrado!"));


        //-----------NUVEM-------------////

        // 1. Gera um nome Unico para o arquivo
        String nomeOriginal = arquivo.getOriginalFilename();
        String extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
        String nomeArquivoUnico = UUID.randomUUID().toString() + extensao;

        // 2. Conecta na Azure e pega o Container
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

        // 3. Faz o upload do arquivo direto para a nuvem
        BlobClient blobClient = containerClient.getBlobClient(nomeArquivoUnico);
        blobClient.upload(arquivo.getInputStream(), arquivo.getSize(), true);

        // 4. Pega o link publico que a Azure gerou
        String urlDoAudioNaNuvem = blobClient.getBlobUrl();

        // 5. Salva no banco de dados
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