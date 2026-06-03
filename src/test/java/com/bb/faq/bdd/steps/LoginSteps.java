package com.bb.faq.bdd.steps;

import com.bb.faq.DTOs.RegistroDTO;
import com.bb.faq.service.UsuarioService;
import io.cucumber.java.pt.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LoginSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

    private String jsonRequest;
    private ResultActions resultActions;

    @Dado("que o usuário preenche o email com {string} e a senha com {string}")
    public void queOUsuarioPreencheOEmailEASenha(String email, String senha) {


        try {
            usuarioService.registrar(new RegistroDTO("Admin Teste", "admin@darkarts.com", "Admin123!"));
        } catch (Exception e) {
        }

        jsonRequest = String.format("""
                {
                    "email": "%s",
                    "senha": "%s"
                }
                """, email, senha);
    }

    @Quando("o usuário solicitar o login")
    public void oUsuarioSolicitarOLogin() throws Exception {
        resultActions = mockMvc.perform(post("/api/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest));
    }

    @Então("o sistema deve autenticar o usuário com status {int}")
    public void oSistemaDeveAutenticarOUsuarioComStatus(int statusEsperado) throws Exception {
        resultActions.andExpect(status().is(statusEsperado));
    }

    @Então("deve retornar um token JWT válido na resposta")
    public void deveRetornarUmTokenJwtValido() throws Exception {
        resultActions.andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Então("o sistema deve recusar a autenticação com status {int}")
    public void oSistemaDeveRecusarAAutenticacaoComStatus(int statusEsperado) throws Exception {
        resultActions.andExpect(status().is(statusEsperado));
    }

    @Então("exibir a mensagem de erro {string}")
    public void exibirAMensagemDeErro(String mensagemEsperada) throws Exception {
        resultActions.andExpect(jsonPath("$.mensagem").value(mensagemEsperada));
    }
}