package com.bb.faq.service;

import com.bb.faq.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;
    private Usuario usuario;
    private final String SECRET_TESTE = "12345678";

    @BeforeEach
    void setup() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", SECRET_TESTE);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@bb.com.br");
        usuario.setNome("Usuário Teste");
    }

    @Test
    @DisplayName("Deve gerar um token válido para um usuário")
    void deveGerarToken() {
        String token = tokenService.gerarToken(usuario);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Deve extrair o subject (e-mail) corretamente de um token válido")
    void deveExtrairSubjectDeTokenValido() {
        String token = tokenService.gerarToken(usuario);

        String subject = tokenService.getSubject(token);

        assertEquals("teste@bb.com.br", subject);
    }

    @Test
    @DisplayName("Deve retornar string vazia ao tentar validar um token inválido ou malformado")
    void deveRetornarVazioParaTokenInvalido() {
        String tokenInvalido = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid.token";

        String subject = tokenService.getSubject(tokenInvalido);

        assertEquals("", subject);
    }

    @Test
    @DisplayName("Deve retornar string vazia para um token nulo")
    void deveRetornarVazioParaTokenNulo() {
        String subject = tokenService.getSubject(null);

        assertEquals("", subject);
    }
}