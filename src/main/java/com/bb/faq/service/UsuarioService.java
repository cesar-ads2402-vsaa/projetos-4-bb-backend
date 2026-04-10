package com.bb.faq.service;



import com.bb.faq.DTOs.LoginDTO;
import com.bb.faq.DTOs.RegistroDTO;
import com.bb.faq.DTOs.TokenResponseDTO;
import com.bb.faq.model.Usuario;
import com.bb.faq.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder; // Aquele que configuramos no SecurityConfig
    private final TokenService tokenService;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    // 1. REGISTRAR
    public void registrar(RegistroDTO dto) {

        // Verifica se o e-mail ja existe
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

    // 2. FAZER LOGIN

    public TokenResponseDTO login(LoginDTO dto) {
        // Busca o usuário pelo e-mail
        Usuario usuario = repository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        // Compara a senha que veio do Next.js com a senha criptografada do Banco
        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new RuntimeException("Senha incorreta!");
        }

        // Se chegou até aqui, a senha está certa. Gera o crachá!
        String token = tokenService.gerarToken(usuario);

        return new TokenResponseDTO(token, usuario.getNome());
    }
}