package com.bb.faq.DTOs;
import jakarta.validation.constraints.NotBlank;

public record TutorialRequestDTO(
        @NotBlank(message = "A pergunta é obrigatória")
        String pergunta,
        @NotBlank(message = "A URL do YouTube é obrigatória")
        String youtubeUrl,
        @NotBlank(message = "A categoria é obrigatória")
        String categoria

) {
}
