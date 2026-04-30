package com.bb.faq.service;

import com.bb.faq.DTOs.*;
import com.bb.faq.model.Usuario;
import com.bb.faq.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }


    public void registrar(RegistroDTO dto) {
        if (repository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("Este e-mail já está cadastrado!");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dto.nome());
        novoUsuario.setEmail(dto.email());
        novoUsuario.setSenha(passwordEncoder.encode(dto.senha()));
        novoUsuario.setCargo(Usuario.Role.USER);

        repository.save(novoUsuario);
    }


    public TokenResponseDTO login(LoginDTO dto) {
        Usuario usuario = repository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new RuntimeException("Senha incorreta!");
        }

        String token = tokenService.gerarToken(usuario);

        return new TokenResponseDTO(token, usuario.getNome(),usuario.getCargo().name());
    }

    public List<UsuarioResponseDTO> listarUsuariosComuns() {
        return repository.findByCargoNot(Usuario.Role.SUPER_ADMIN).stream()
                .map(usuario -> new UsuarioResponseDTO(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getCargo().name()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void promoverParaAdmin(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        usuario.setCargo(Usuario.Role.ADMIN);
        repository.save(usuario);
    }
    @Transactional
    public void rebaixarOuDeletarAdmin(Long idAlvo) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof Usuario)) {
            throw new RuntimeException("Acesso negado: Usuário não autenticado.");
        }
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        Usuario alvo = repository.findById(idAlvo)
                .orElseThrow(() -> new RuntimeException("Usuário alvo não encontrado!"));

        if (alvo.getCargo() == Usuario.Role.SUPER_ADMIN) {
            throw new RuntimeException("Acesso Negado: O SUPER_ADMIN não pode ser rebaixado ou alterado.");
        }

        if (alvo.getCargo() == Usuario.Role.ADMIN && usuarioLogado.getCargo() != Usuario.Role.SUPER_ADMIN) {
            throw new RuntimeException("Acesso Negado: Apenas o SUPER_ADMIN pode rebaixar outro ADMIN.");
        }

        alvo.setCargo(Usuario.Role.USER);
        repository.save(alvo);
    }

    @Transactional
    public void trocarSenha(TrocarSenhaDTO dto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof Usuario)) {
            throw new RuntimeException("Acesso negado: Usuário não autenticado.");
        }

        Usuario usuarioSessao = (Usuario) authentication.getPrincipal();

        Usuario usuarioBanco = repository.findById(usuarioSessao.getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado no banco de dados!"));

        if (!passwordEncoder.matches(dto.senhaAtual(), usuarioBanco.getSenha())) {
            throw new RuntimeException("A senha atual informada está incorreta!");
        }

        String novaSenhaCriptografada = passwordEncoder.encode(dto.novaSenha());
        usuarioBanco.setSenha(novaSenhaCriptografada);

        repository.save(usuarioBanco);
    }
}
