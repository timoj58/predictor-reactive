package com.timmytime.predictorplayersreactive.router;

import com.timmytime.predictorplayersreactive.handler.DataHandler;
import com.timmytime.predictorplayersreactive.request.PlayerEventOutcomeCsv;
import com.timmytime.predictorplayersreactive.service.TensorflowDataService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

//me testing webflux tests not much use but can leave as example.
@ExtendWith(SpringExtension.class)
@WebFluxTest(DataFunction.class)
@ContextConfiguration(classes = {DataFunction.class, DataHandler.class})
class DataFunctionTest {

    @Autowired
    private ApplicationContext context;

    private WebTestClient webTestClient;

    @MockBean
    private TensorflowDataService tensorflowDataService;

    @Test
    public void getDataTest() {
        String fromDate = "01-01-2010";
        String toDate = "01-01-2011";

        PlayerEventOutcomeCsv playerEventOutcomeCsv
                = new PlayerEventOutcomeCsv();

        playerEventOutcomeCsv.setGoals(1);

        when(
                tensorflowDataService.getPlayerCsv(fromDate, toDate)
        ).thenReturn(
                Arrays.asList(playerEventOutcomeCsv)
        );

        webTestClient = WebTestClient.bindToApplicationContext(context).build();

        webTestClient.get()
                .uri("/data/" + fromDate + "/" + toDate)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PlayerEventOutcomeCsv.class)
                .value(p -> Assertions.assertThat(p.get(0).getGoals()).isEqualTo(1));

        webTestClient.get()
                .uri("/data/01-01-2021/01-01-2022")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PlayerEventOutcomeCsv.class)
                .value(l -> assertTrue(l.isEmpty()));

    }

}