package com.bb.faq.DTOs;

import java.time.LocalDateTime;

public record TutorialResponseDTO(
        Long id,
        String pergunta,
        String youtubeUrl,
        LocalDateTime dataCriacao
) {
}
