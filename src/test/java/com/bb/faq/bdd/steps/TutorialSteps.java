package com.bb.faq.bdd.steps;

import io.cucumber.java.pt.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TutorialSteps {

    @Autowired
    private MockMvc mockMvc;

    private String pergunta;
    private String youtubeUrl;
    private String categoria;

    private ResultActions resultActions;

    @Dado("que o administrador está autenticado e na tela de gerenciamento de FAQ")
    public void contextoAutenticado() {
    }

    @Dado("que preenche a pergunta com {string}")
    public void preencherPergunta(String pergunta) {
        this.pergunta = pergunta;
    }

    @Dado("preenche a URL do YouTube com {string}")
    public void preencherUrl(String url) {
        this.youtubeUrl = url;
    }

    @Dado("seleciona a categoria {string}")
    public void selecionarCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Quando("solicitar a criação do tutorial")
    public void solicitarCriacao() throws Exception {
        String p = (pergunta != null) ? pergunta.trim() : "";
        String u = (youtubeUrl != null) ? youtubeUrl.trim() : "";
        String c = (categoria != null) ? categoria.trim() : "";


        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        jsonBuilder.append("\"pergunta\": ").append(p.isEmpty() ? "null" : "\"" + p + "\"").append(",");
        jsonBuilder.append("\"youtubeUrl\": ").append(u.isEmpty() ? "null" : "\"" + u + "\"").append(",");
        jsonBuilder.append("\"categoria\": ").append(c.isEmpty() ? "null" : "\"" + c + "\"");
        jsonBuilder.append("}");

        resultActions = mockMvc.perform(post("/api/tutoriais")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBuilder.toString()));
    }

    @Então("o sistema deve salvar o novo tutorial no banco de dados")
    public void sistemaDeveSalvar() throws Exception {
        resultActions.andExpect(status().isCreated());
    }

    @Então("deve retornar os dados confirmando a criação, incluindo o ID gerado e a data de criação")
    public void verificarDadosConfirmacao() throws Exception {
        resultActions.andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.dataCriacao").exists());
    }

    @Então("o sistema deve recusar a operação")
    public void sistemaDeveRecusar() throws Exception {
        resultActions.andExpect(status().isBadRequest());
    }
}