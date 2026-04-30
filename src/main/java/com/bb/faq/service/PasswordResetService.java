package com.bb.faq.service;

import com.bb.faq.model.PasswordResetToken;
import com.bb.faq.model.Usuario;
import com.bb.faq.repository.PasswordResetTokenRepository;
import com.bb.faq.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    @Value("${app.frontend.url}")
    private String frontendUrl;

    public PasswordResetService(UsuarioRepository usuarioRepository,
                                PasswordResetTokenRepository tokenRepository,
                                JavaMailSender mailSender,
                                PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void solicitarRecuperacaoSenha(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Se o e-mail existir, um link de recuperação foi enviado."));

        String token = UUID.randomUUID().toString();

        tokenRepository.deleteByUsuario(usuario);

        PasswordResetToken resetToken = new PasswordResetToken(
                token,
                usuario,
                LocalDateTime.now().plusMinutes(30)
        );
        tokenRepository.save(resetToken);

        String linkRecuperacao = frontendUrl + "/resetar-senha?token=" + token;

        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom("faqbb@darkartsbm.com");
        mensagem.setTo(usuario.getEmail());
        mensagem.setSubject("TakeOff BB - Recuperação de Senha");
        mensagem.setText("Olá, " + usuario.getNome() + "!\n\n" +
                "Você solicitou a recuperação da sua senha. Clique no link abaixo para criar uma nova:\n" +
                linkRecuperacao + "\n\n" +
                "Este link é válido por 30 minutos. Se você não solicitou isso, ignore este e-mail.");

        mailSender.send(mensagem);
    }

    @Transactional
    public void resetarSenha(String token, String novaSenha) {

        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido ou não encontrado!"));

        if (resetToken.getDataExpiracao().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("Este link de recuperação já expirou. Solicite um novo.");
        }

        Usuario usuario = resetToken.getUsuario();
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
        tokenRepository.delete(resetToken);
    }
}