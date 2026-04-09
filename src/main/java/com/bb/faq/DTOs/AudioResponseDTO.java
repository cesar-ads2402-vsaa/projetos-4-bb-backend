package com.bb.faq.DTOs;

import java.time.LocalDateTime;

public record AudioResponseDTO(
        Long id,
        String caminhoArquivo,
        LocalDateTime dataCriacao,
        Long tutorialId,
        Integer votos,
        String idioma,
        String nomeAutor
) {}