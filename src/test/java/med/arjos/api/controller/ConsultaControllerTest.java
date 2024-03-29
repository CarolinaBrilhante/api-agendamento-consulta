package med.arjos.api.controller;

import med.arjos.api.domain.consulta.AgendaDeConsultas;
import med.arjos.api.domain.consulta.DadosAgendamentoConsulta;
import med.arjos.api.domain.consulta.DadosDetalhamentoConsulta;
import med.arjos.api.domain.consulta.MotivoCancelamento;
import med.arjos.api.domain.medico.Especialidade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class ConsultaControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<DadosAgendamentoConsulta> dadosAgendamentoConsultaJson;

    @Autowired
    private JacksonTester<DadosDetalhamentoConsulta> dadosDetalhamentoConsultaJson;
    @MockBean
    private AgendaDeConsultas agendaDeConsultas;
    @Test
    @DisplayName("Deveria devolver código http 400 quando informações estão inválidas")
    @WithMockUser
    void agendarCenario1() throws Exception {
        var response = mvc.perform(post("/consultas"))
        .andReturn().getResponse();

        assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST.value());

    }
    @Test
    @DisplayName("Deveria devolver código http 200 quando informações estão válidas")
    @WithMockUser
    void agendarCenario2() throws Exception{
        var data = LocalDateTime.now().plusHours(1);
        var especialidade = Especialidade.CARDIOLOGIA;
        var motivoCancelamento = MotivoCancelamento.NULL;
        var dadosDetalhamento =new DadosDetalhamentoConsulta(null, 1l, 5l, data, motivoCancelamento);

        when(agendaDeConsultas.agendar(any())).thenReturn(dadosDetalhamento);

        var response = mvc
                .perform(
                        post("/consultas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(dadosAgendamentoConsultaJson.write(
                                        new DadosAgendamentoConsulta(1l, 5l, data, especialidade, motivoCancelamento)
                                ).getJson())
                )
                .andReturn().getResponse();
        assertEquals(response.getStatus(), HttpStatus.OK.value());

        var jsonEsperado = dadosDetalhamentoConsultaJson.write(
                dadosDetalhamento
        ).getJson();

        assertEquals(response.getContentAsString(), jsonEsperado);
    }

}