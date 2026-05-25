package com.bb.faq.controller;

import com.bb.faq.DTOs.*;
import com.bb.faq.service.PasswordResetService;
import com.bb.faq.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService service;

    @Mock
    private PasswordResetService password;

    @InjectMocks
    private UsuarioController controller;

    private RegistroDTO registroDTO;
    private LoginDTO loginDTO;
    private TokenResponseDTO tokenResponse;
    private UsuarioResponseDTO usuarioResponse;

    @BeforeEach
    void setup() {
        registroDTO = new RegistroDTO("Novo Usuário", "novo@teste.com", "senha123");
        loginDTO = new LoginDTO("novo@teste.com", "senha123");
        tokenResponse = new TokenResponseDTO("jwt-token", "Novo Usuário", "USER");
        usuarioResponse = new UsuarioResponseDTO(1L, "Novo Usuário", "novo@teste.com", "USER");
    }

    @Test
    @DisplayName("Deve registrar usuário chamando o service")
    void deveRegistrarUsuario() {
        assertDoesNotThrow(() -> controller.registrar(registroDTO));
        verify(service, times(1)).registrar(registroDTO);
    }

    @Test
    @DisplayName("Deve realizar login e retornar token")
    void deveRealizarLogin() {
        when(service.login(loginDTO)).thenReturn(tokenResponse);

        TokenResponseDTO resultado = controller.login(loginDTO);

        assertEquals("jwt-token", resultado.token());
        assertEquals("Novo Usuário", resultado.nomeUsuario());
        verify(service, times(1)).login(loginDTO);
    }

    @Test
    @DisplayName("Deve promover usuário e retornar status 204")
    void devePromoverUsuario() {
        ResponseEntity<Void> resposta = controller.promoverUsuario(1L);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        verify(service, times(1)).promoverParaAdmin(1L);
    }

    @Test
    @DisplayName("Deve rebaixar usuário e retornar status 200")
    void deveRebaixarUsuario() {
        ResponseEntity<Void> resposta = controller.rebaixarUsuario(1L);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(service, times(1)).rebaixarOuDeletarAdmin(1L);
    }

    @Test
    @DisplayName("Deve tratar RuntimeException e retornar status 400")
    void deveTratarRuntimeException() {
        ResponseEntity<String> resposta = controller.handleRuntimeException(
                new RuntimeException("Erro de validação")
        );

        assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
        assertEquals("Erro de validação", resposta.getBody());
    }

    @Test
    @DisplayName("Deve listar usuários comuns")
    void deveListarUsuariosComuns() {
        when(service.listarUsuariosComuns()).thenReturn(List.of(usuarioResponse));

        List<UsuarioResponseDTO> resultado = controller.buscarUsuarios();

        assertEquals(1, resultado.size());
        assertEquals("USER", resultado.getFirst().cargo());
        verify(service, times(1)).listarUsuariosComuns();
    }

    @Test
    @DisplayName("Deve trocar senha e retornar mensagem de sucesso")
    void deveTrocarSenha() {
        TrocarSenhaDTO dto = new TrocarSenhaDTO("senhaAtual", "novaSenha123");

        ResponseEntity<String> resposta = controller.trocarSenha(dto);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("Senha alterada com sucesso!", resposta.getBody());
        verify(service, times(1)).trocarSenha(dto);
    }

    @Test
    @DisplayName("Deve solicitar recuperação de senha")
    void deveSolicitarRecuperacaoSenha() {
        EsqueciSenhaDTO dto = new EsqueciSenhaDTO("novo@teste.com");

        ResponseEntity<String> resposta = controller.solicitarRecuperacao(dto);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertTrue(resposta.getBody().contains("link de recuperação"));
        verify(password, times(1)).solicitarRecuperacaoSenha("novo@teste.com");
    }

    @Test
    @DisplayName("Deve resetar senha com token")
    void deveResetarSenha() {
        ResetarSenhaDTO dto = new ResetarSenhaDTO("token-abc", "novaSenha456");

        ResponseEntity<String> resposta = controller.resetarSenha(dto);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertTrue(resposta.getBody().contains("alterada com sucesso"));
        verify(password, times(1)).resetarSenha("token-abc", "novaSenha456");
    }
}
