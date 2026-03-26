package com.bb.faq.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audios")
public class Audio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Aqui não salvamos o áudio pesado, apenas o TEXTO de onde ele está salvo na pasta!
    @Column(name = "caminho_arquivo", nullable = false)
    private String caminhoArquivo;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    private Integer votos = 0; // Todo áudio começa com 0 votos


    @ManyToOne
    @JoinColumn(name = "tutorial_id", nullable = false)
    private Tutorial tutorial;
    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Usuario autor;


    public Audio() {
        this.dataCriacao = LocalDateTime.now();
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCaminhoArquivo() { return caminhoArquivo; }
    public void setCaminhoArquivo(String caminhoArquivo) { this.caminhoArquivo = caminhoArquivo; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public Tutorial getTutorial() { return tutorial; }
    public void setTutorial(Tutorial tutorial) { this.tutorial = tutorial; }
    public Integer getVotos() { return votos; }
    public void setVotos(Integer votos) { this.votos = votos; }
    public Usuario getAutor() { return autor; }
    public void setAutor(Usuario autor) { this.autor = autor; }
}