package com.bb.faq.model;

import jakarta.persistence.*;
import jakarta.persistence.Id;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
    @Table(name = "tutoriais")
    public class Tutorial {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String pergunta;

        @Column(nullable = false, name = "youtube_url")
        private String youtubeUrl;

        @Column(name = "data_criacao")
        private LocalDateTime dataCriacao;

        @OneToMany(mappedBy = "tutorial", cascade = CascadeType.REMOVE, orphanRemoval = true)
        private List<Audio> audios = new ArrayList<>();


    public Tutorial() {
            this.dataCriacao = LocalDateTime.now();
        }

        // Getters e Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getPergunta() { return pergunta; }
        public void setPergunta(String pergunta) { this.pergunta = pergunta; }

        public String getYoutubeUrl() { return youtubeUrl; }
        public void setYoutubeUrl(String youtubeUrl) { this.youtubeUrl = youtubeUrl; }

        public LocalDateTime getDataCriacao() { return dataCriacao; }
        public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

        public List<Audio> getAudios() {return audios;}

        public void setAudios(List<Audio> audios) {this.audios = audios;}
}

