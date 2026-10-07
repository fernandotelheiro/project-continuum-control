package br.edu.fernandotelheiro.continuumcontrol.api;

import br.edu.fernandotelheiro.continuumcontrol.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class ApiSegurancaTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @BeforeEach
    void configurarMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void semCredencialRetorna401() throws Exception {
        mockMvc.perform(
                        get("/missoes")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(
                        content().contentType(
                                MediaType.APPLICATION_PROBLEM_JSON
                        )
                );
    }

    @Test
    @WithMockUser(roles = "OPERADOR")
    void operadorNaoPodeIniciarMissao() throws Exception {
        mockMvc.perform(
                        post("/missoes/1/iniciar")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(
            username = "agente-sem-vinculo",
            roles = "AGENTE"
    )
    void agenteSemParticipacaoNaoRegistraIntervencao() throws Exception {

        String json = """
                {
                  "agenteId": 1,
                  "acao": "Teste de seguranca",
                  "justificativa": "Validar autorizacao por recurso",
                  "impacto": "MINIMO",
                  "resultado": "Teste"
                }
                """;

        mockMvc.perform(
                        post("/missoes/1/intervencoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "OPERADOR")
    void payloadInvalidoRetorna400() throws Exception {
        mockMvc.perform(
                        post("/missoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        content().contentType(
                                MediaType.APPLICATION_PROBLEM_JSON
                        )
                )
                .andExpect(
                        jsonPath("$.erros.codigo").exists()
                );
    }

    @Test
    @WithMockUser
    void missaoInexistenteRetorna404() throws Exception {
        mockMvc.perform(
                        get("/missoes/999")
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        content().contentType(
                                MediaType.APPLICATION_PROBLEM_JSON
                        )
                );
    }
}