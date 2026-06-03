package com.bb.faq.bdd.steps;

import io.cucumber.java.pt.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TutorialSteps {

    @Autowired
    private MockMvc mockMvc;

    private String jsonRequest;
    private ResultActions resultActions;

    @Dado("que eu tenho os dados de um tutorial com a pergunta em branco")
    public void prepararDadosComPerguntaEmBranco() {
        jsonRequest = """
                {
                    "pergunta": "",
                    "youtubeUrl": "https://youtube.com/video",
                    "categoria": "Dúvidas Frequentes"
                }
                """;
    }

    @Quando("eu enviar a requisição para salvar")
    public void enviarRequisicaoParaSalvar() throws Exception {
        resultActions = mockMvc.perform(post("/api/tutoriais")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest));
    }

    @Então("o sistema deve retornar o status {int}")
    public void verificarStatus(int statusEsperado) throws Exception {
        resultActions.andExpect(status().is(statusEsperado));
    }
}