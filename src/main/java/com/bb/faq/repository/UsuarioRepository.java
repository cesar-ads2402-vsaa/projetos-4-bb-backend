package com.bb.faq.repository;



import com.bb.faq.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // O Spring cria a busca SQL mágica: SELECT * FROM usuarios WHERE email = ?
    Optional<Usuario> findByEmail(String email);
}