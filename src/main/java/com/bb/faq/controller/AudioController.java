package com.bb.faq.controller;

import com.bb.faq.DTOs.AudioResponseDTO;
import com.bb.faq.service.AudioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/audio")
public class AudioController {

    private final AudioService audioService;

    public AudioController(AudioService audioService) {
        this.audioService = audioService;
    }


    @PostMapping(value = "/{tutorialId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AudioResponseDTO fazerUploadAudio(
            @PathVariable Long tutorialId,
            @RequestParam("arquivo") MultipartFile arquivo) throws IOException {


        return audioService.salvarAudio(tutorialId, arquivo);
    }
    @PatchMapping("/{audioId}/upvote")
    public AudioResponseDTO votarNoAudio(@PathVariable Long audioId) {
        return audioService.adicionarVoto(audioId);
    }
    @GetMapping("/{tutorialId}")
    public List<AudioResponseDTO> buscarAudiosOrdenados(@PathVariable Long tutorialId) {
        return audioService.listarAudiosDoTutorial(tutorialId);
    }
}