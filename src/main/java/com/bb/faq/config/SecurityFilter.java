package com.bb.faq.config;

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

                // 1. O banco devolve a CAIXA (Optional)
                var usuarioOptional = usuarioRepository.findByEmail(subject);

                // 2. Verificamos se a caixa tem alguém dentro (isPresent)
                if (usuarioOptional.isPresent()) {

                    // 3. Tiramos o usuário de dentro da caixa!
                    var usuario = usuarioOptional.get();

                    // 4. Cria a permissão e avisa o Spring que o usuário real está logado
                    // No SecurityFilter.java, mude a linha da autoridade:
                    var permissoes = List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getCargo().name()));
                    var authentication = new UsernamePasswordAuthenticationToken(usuario, null, permissoes);
                }
            }
        }

        // Se não tiver token, ou se não achar o usuário, a requisição segue e o SecurityConfig decide o que fazer
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