package com.bb.faq.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> {
                    // 1. ZONA DO ADMIN
                    req.requestMatchers(HttpMethod.POST, "/api/tutoriais").hasAnyRole("ADMIN","SUPER_ADMIN");
                    req.requestMatchers(HttpMethod.DELETE, "/api/tutoriais/**").hasAnyRole("ADMIN","SUPER_ADMIN");
                    req.requestMatchers("/api/audio/moderacao/**").hasAnyRole("ADMIN","SUPER_ADMIN");
                    req.requestMatchers(HttpMethod.DELETE, "/api/audio/**").hasAnyRole("ADMIN","SUPER_ADMIN");

                    // 2. ZONA PÚBLICA
                    req.requestMatchers(HttpMethod.GET, "/api/tutoriais").permitAll();
                    req.requestMatchers(HttpMethod.GET, "/api/tutoriais/**").permitAll();
                    req.requestMatchers(HttpMethod.GET, "/api/audio/**").permitAll();
                    req.requestMatchers(HttpMethod.POST, "/api/usuarios/login").permitAll();
                    req.requestMatchers(HttpMethod.POST, "/api/usuarios/cadastro").permitAll();

                    // 3. CORS E ERROS
                    req.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    req.requestMatchers("/error").permitAll();

                    // 🟡 4. ZONA DO USUÁRIO LOGADO (Gravar áudio, dar like, etc)
                    req.anyRequest().authenticated();
                })
                // A MÁGICA ACONTECE AQUI: Coloca o nosso filtro ANTES do filtro padrão do Spring
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // 4. A LISTA VIP DO CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Aqui tem que estar a URL Exata do Front
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));

        // Libera os métodos que o seu frontend vai usar
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Libera os cabeçalhos (Authorization é onde vai o Token JWT)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));

        // Permite o envio de credenciais (Tokens/Cookies)
        configuration.setAllowCredentials(true);

        // Aplica essa regra para TODOS os endpoints da API (/**)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // Ensina o Spring a criptografar as senhas no banco de dados!
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}