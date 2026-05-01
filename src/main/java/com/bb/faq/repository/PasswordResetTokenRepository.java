package com.bb.faq.repository;

import com.bb.faq.model.PasswordResetToken;
import com.bb.faq.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUsuario(Usuario usuario);
}