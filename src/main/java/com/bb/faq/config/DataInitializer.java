package com.bb.faq.config;

import com.bb.faq.model.Usuario;
import com.bb.faq.model.Usuario.Role;
import com.bb.faq.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String emailAdmin = "admin@faq.com";

        if (usuarioRepository.findByEmail(emailAdmin).isEmpty()) {
            System.out.println("Semeando o banco: Criando o primeiro Administrador...");

            Usuario admin = new Usuario();
            admin.setNome("Administrador Supremo");
            admin.setEmail(emailAdmin);
            admin.setSenha(passwordEncoder.encode("senha123"));
            admin.setCargo(Role.ADMIN);

            usuarioRepository.save(admin);

            System.out.println("Admin criado com sucesso! E-mail: " + emailAdmin);
        }
    }
}