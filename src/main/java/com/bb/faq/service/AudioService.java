package com.bb.faq.service;

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


    @Value("${app.upload.dir}")
    private String uploadDir;

    public AudioService(AudioRepository audioRepository, TutorialRepository tutorialRepository) {
        this.audioRepository = audioRepository;
        this.tutorialRepository = tutorialRepository;
    }

    public AudioResponseDTO salvarAudio(Long tutorialId, MultipartFile arquivo) throws IOException {

        // 1. Verifica se o vídeo (Tutorial) realmente existe no banco de dados
        Tutorial tutorial = tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new RuntimeException("Tutorial não encontrado!"));


        //-----------TROCAR POR NUVEM-------------////
        // 2. Cria a pasta física no computador se ela ainda não existir
        Path caminhoPasta = Paths.get(uploadDir);
        if (!Files.exists(caminhoPasta)) {
            Files.createDirectories(caminhoPasta);
        }
        // 3. Gera um nome único para o arquivo usando UUID
        String nomeOriginal = arquivo.getOriginalFilename();
        String extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
        String nomeArquivoUnico = UUID.randomUUID().toString() + extensao;

        // 4. Salva o arquivo fisicamente na pasta
        Path caminhoDestino = caminhoPasta.resolve(nomeArquivoUnico);
        Files.copy(arquivo.getInputStream(), caminhoDestino, StandardCopyOption.REPLACE_EXISTING);

        //------------------------------------------------//



        // 5. Agora que o arquivo está seguro no disco, salvamos o caminho no Banco de Dados (SQLite)
        Audio novoAudio = new Audio();
        // Salvamos um caminho relativo que o Front-end consiga acessar depois
        novoAudio.setCaminhoArquivo("/" + uploadDir + "/" + nomeArquivoUnico);
        novoAudio.setTutorial(tutorial);

        Audio audioSalvo = audioRepository.save(novoAudio);

        // 6. Retorna o DTO confirmando a operação
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

        // 3. Guarda a alteração (como usamos @Transactional, o Spring pode até fazer isto automaticamente, mas o .save() garante)
        Audio audioAtualizado = audioRepository.save(audio);

        // 4. Devolve o DTO atualizado para o Frontend mostrar o novo número
        return new AudioResponseDTO(
                audioAtualizado.getId(),
                audioAtualizado.getCaminhoArquivo(),
                audioAtualizado.getDataCriacao(),
                audioAtualizado.getTutorial().getId(),
                audioAtualizado.getVotos()
        );
    }
    public List<AudioResponseDTO> listarAudiosDoTutorial(Long tutorialId) {
        // Usa a pesquisa mágica do Spring que já traz ordenado pelos mais votados!
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