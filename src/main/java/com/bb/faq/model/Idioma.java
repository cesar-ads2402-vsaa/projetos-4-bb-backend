package com.bb.faq.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "idiomas")
@Data
public class Idioma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome; // Ex: "Português", "Inglês"

    @Column(nullable = false, unique = true)
    private String codigo; // Ex: "pt-BR", "en-US"
}