package com.bb.faq.DTOs;

import com.bb.faq.model.Idioma;

public record IdiomaResponseDTO(Long id, String nome, String codigo) {
    public IdiomaResponseDTO(Idioma idioma) {
        this(idioma.getId(), idioma.getNome(), idioma.getCodigo());
    }
}
