package com.bb.faq.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.bb.faq.DTOs.TutorialRequestDTO;
import com.bb.faq.DTOs.TutorialResponseDTO;
import com.bb.faq.model.Audio;
import com.bb.faq.model.Tutorial;
import com.bb.faq.repository.TutorialRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TutorialService {

    private final TutorialRepository tutorialRepository;
    private final BlobContainerClient containerClient;

    public TutorialService(TutorialRepository tutorialRepository, BlobContainerClient containerClient) {
        this.tutorialRepository = tutorialRepository;
        this.containerClient = containerClient;
    }


    public List<TutorialResponseDTO> listarTodos() {
        return tutorialRepository.findAll()
                .stream()
                .map(t -> new TutorialResponseDTO(
                        t.getId(),
                        t.getPergunta(),
                        t.getYoutubeUrl(),
                        t.getDataCriacao()
                ))
                .collect(Collectors.toList());
    }


    @Transactional
    public TutorialResponseDTO criarTutorial(TutorialRequestDTO dto) {


        Tutorial novoTutorial = new Tutorial();
        novoTutorial.setPergunta(dto.pergunta());
        novoTutorial.setYoutubeUrl(dto.youtubeUrl());

        Tutorial tutorialSalvo = tutorialRepository.save(novoTutorial);

        return new TutorialResponseDTO(
                tutorialSalvo.getId(),
                tutorialSalvo.getPergunta(),
                tutorialSalvo.getYoutubeUrl(),
                tutorialSalvo.getDataCriacao()
        );
    }

    @Transactional
    public void deletarTutorial(Long tutorialId) {
        Tutorial tutorial = tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new RuntimeException("Tutorial não encontrado!"));

        for (Audio audio : tutorial.getAudios()) {
            if (audio.getCaminhoArquivo() != null) {
                String nomeArquivoNaAzure = audio.getCaminhoArquivo().substring(
                        audio.getCaminhoArquivo().lastIndexOf("/") + 1
                );
                BlobClient blobClient = containerClient.getBlobClient(nomeArquivoNaAzure);
                blobClient.deleteIfExists();
            }
        }

        tutorialRepository.delete(tutorial);
    }
}
