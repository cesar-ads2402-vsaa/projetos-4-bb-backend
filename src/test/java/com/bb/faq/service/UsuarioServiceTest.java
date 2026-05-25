package com.bb.faq.service;

import com.bb.faq.DTOs.*;
import com.bb.faq.model.Usuario;
import com.bb.faq.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UsuarioService service;

    private Usuario usuarioPadrao;

    @BeforeEach
    void setup() {
        usuarioPadrao = new Usuario();
        usuarioPadrao.setId(1L);
        usuarioPadrao.setNome("Robson");
        usuarioPadrao.setEmail("robson@teste.com");
        usuarioPadrao.setSenha("senhaCriptografada");
        usuarioPadrao.setCargo(Usuario.Role.USER);
    }

    @Test
    @DisplayName("Deve registrar um usuário com sucesso")
    void deveRegistrarUsuario() {
        RegistroDTO dto = new RegistroDTO("Novo User", "novo@teste.com", "senha123");

        when(repository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.senha())).thenReturn("hash123");

        assertDoesNotThrow(() -> service.registrar(dto));

        verify(repository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar erro ao registrar e-mail duplicado")
    void deveLancarErroEmailDuplicado() {
        RegistroDTO dto = new RegistroDTO("Robson", "robson@teste.com", "123");
        when(repository.findByEmail(dto.email())).thenReturn(Optional.of(usuarioPadrao));

        RuntimeException erro = assertThrows(RuntimeException.class, () -> service.registrar(dto));
        assertEquals("Este e-mail já está cadastrado!", erro.getMessage());
    }

    @Test
    @DisplayName("Deve realizar login com sucesso e retornar token")
    void deveFazerLogin() {
        LoginDTO dto = new LoginDTO("robson@teste.com", "senha123");

        when(repository.findByEmail(dto.email())).thenReturn(Optional.of(usuarioPadrao));
        when(passwordEncoder.matches(dto.senha(), usuarioPadrao.getSenha())).thenReturn(true);
        when(tokenService.gerarToken(usuarioPadrao)).thenReturn("token-jwt-valido");

        TokenResponseDTO resultado = service.login(dto);

        assertNotNull(resultado);
        assertEquals("token-jwt-valido", resultado.token());
        assertEquals("USER", resultado.cargo());
    }

    @Test
    @DisplayName("Deve promover um usuário a ADMIN")
    void devePromoverParaAdmin() {
        when(repository.findById(1L)).thenReturn(Optional.of(usuarioPadrao));

        service.promoverParaAdmin(1L);

        assertEquals(Usuario.Role.ADMIN, usuarioPadrao.getCargo());
        verify(repository).save(usuarioPadrao);
    }

    @Test
    @DisplayName("Não deve permitir rebaixar o SUPER_ADMIN")
    void naoDeveRebaixarSuperAdmin() {
        Usuario superAdmin = new Usuario();
        superAdmin.setCargo(Usuario.Role.SUPER_ADMIN);

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(new Usuario()); // Alguém logado

        when(repository.findById(99L)).thenReturn(Optional.of(superAdmin));

        RuntimeException erro = assertThrows(RuntimeException.class, () -> service.rebaixarOuDeletarAdmin(99L));
        assertEquals("Acesso Negado: O SUPER_ADMIN não pode ser rebaixado ou alterado.", erro.getMessage());
    }
}