package com.bb.faq.model;


import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    public enum Role { ADMIN, USER }

    @Enumerated(EnumType.STRING)
    private Role cargo = Role.USER; // Padrão é usuário comum

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    public Role getCargo() {
        return cargo;
    }

    public void setCargo(Role cargo) {
        this.cargo = cargo;
    }

    @Column(nullable = false)
    private String senha;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}