package com.bb.faq.config; // Ajuste para o seu pacote correto

import com.bb.faq.repository.UsuarioRepository;
import com.bb.faq.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            var subject = tokenService.getSubject(tokenJWT);

            if (subject != null && !subject.isEmpty()) {
                // ATENÇÃO: Verifique se o seu repositório realmente tem esse método "findByEmail"
                var usuario = usuarioRepository.findByEmail(subject);

                if (usuario != null) {
                    // Cria uma permissão básica
                    var permissoes = List.of(new SimpleGrantedAuthority("ROLE_USER"));

                    // Avisa o Spring que o usuário está logado!
                    var authentication = new UsernamePasswordAuthenticationToken(usuario, null, permissoes);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}