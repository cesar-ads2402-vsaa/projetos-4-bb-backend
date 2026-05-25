package com.bb.faq.service;

import com.bb.faq.model.PasswordResetToken;
import com.bb.faq.model.Usuario;
import com.bb.faq.repository.PasswordResetTokenRepository;
import com.bb.faq.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordResetTokenRepository tokenRepository;
    @Mock private JavaMailSender mailSender;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService service;

    private Usuario usuarioMock;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(service, "frontendUrl", "http://localhost:3000");

        usuarioMock = new Usuario();
        usuarioMock.setEmail("teste@bb.com.br");
        usuarioMock.setNome("Robson");
    }

    @Test
    @DisplayName("Deve gerar token e disparar e-mail ao solicitar recuperação")
    void deveSolicitarRecuperacaoComSucesso() {
        when(usuarioRepository.findByEmail("teste@bb.com.br")).thenReturn(Optional.of(usuarioMock));

        service.solicitarRecuperacaoSenha("teste@bb.com.br");

        verify(tokenRepository, times(1)).deleteByUsuario(usuarioMock); // Limpa tokens antigos
        verify(tokenRepository, times(1)).save(any(PasswordResetToken.class)); // Salva o novo
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class)); // Tenta enviar e-mail
    }

    @Test
    @DisplayName("Deve resetar a senha com sucesso quando o token é válido")
    void deveResetarSenhaComSucesso() {
        String tokenValido = "token-123";
        PasswordResetToken resetToken = new PasswordResetToken(
                tokenValido,
                usuarioMock,
                LocalDateTime.now().plusMinutes(10)
        );

        when(tokenRepository.findByToken(tokenValido)).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode("novaSenha")).thenReturn("senhaCripto");

        service.resetarSenha(tokenValido, "novaSenha");

        assertEquals("senhaCripto", usuarioMock.getSenha());
        verify(usuarioRepository, times(1)).save(usuarioMock);
        verify(tokenRepository, times(1)).delete(resetToken); // Token deve ser apagado após o uso
    }

    @Test
    @DisplayName("Deve lançar erro e apagar token se ele estiver expirado")
    void deveLancarErroTokenExpirado() {
        String tokenExpirado = "token-velho";
        PasswordResetToken resetToken = new PasswordResetToken(
                tokenExpirado,
                usuarioMock,
                LocalDateTime.now().minusMinutes(1) // Já expirou
        );

        when(tokenRepository.findByToken(tokenExpirado)).thenReturn(Optional.of(resetToken));
        
        RuntimeException erro = assertThrows(RuntimeException.class, () ->
                service.resetarSenha(tokenExpirado, "novaSenha")
        );

        assertTrue(erro.getMessage().contains("expirou"));
        verify(tokenRepository, times(1)).delete(resetToken); // Limpeza de segurança
        verify(usuarioRepository, never()).save(any()); // Não pode salvar a senha
    }

    @Test
    @DisplayName("Deve lançar erro se o e-mail não existir no banco")
    void deveLancarErroEmailInexistente() {
        when(usuarioRepository.findByEmail("naoexiste@bb.com.br")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                service.solicitarRecuperacaoSenha("naoexiste@bb.com.br")
        );
    }
}