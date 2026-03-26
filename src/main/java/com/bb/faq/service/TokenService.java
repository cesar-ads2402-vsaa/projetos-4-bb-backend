package com.bb.faq.service;



import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.bb.faq.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    // Pega a senha secreta do application.properties
    @Value("${api.security.token.secret}")
    private String secret;

    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("faq-api") // Quem emitiu o token
                    .withSubject(usuario.getEmail()) // Quem é o dono do token (vamos usar o e-mail como identificador)
                    .withClaim("id", usuario.getId()) // Guardamos o ID dentro do crachá para facilitar depois!
                    .withExpiresAt(gerarDataExpiracao()) // Crachá tem validade
                    .sign(algoritmo); // Assina e carimba
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    // O token vale por 2 horas. Depois disso, o usuário tem que logar de novo.
    private Instant gerarDataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
    // NOVO MÉTODO: Ensina o sistema a ler o crachá e descobrir de quem é
    public String getSubject(String tokenJWT) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                    .withIssuer("faq-api")
                    .build()
                    .verify(tokenJWT)
                    .getSubject(); // Devolve o e-mail que guardamos no token
        } catch (Exception exception) {
            // Se o token for inválido, expirado ou falso, retorna vazio
            return "";
        }
    }
}