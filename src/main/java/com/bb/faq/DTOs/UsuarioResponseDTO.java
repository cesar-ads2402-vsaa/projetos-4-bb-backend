package com.bb.faq.DTOs;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        String cargo
) {}