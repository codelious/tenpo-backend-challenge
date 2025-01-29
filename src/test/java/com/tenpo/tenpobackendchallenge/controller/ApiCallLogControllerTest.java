package com.tenpo.tenpobackendchallenge.controller;

import com.tenpo.tenpobackendchallenge.model.ApiCallLog;
import com.tenpo.tenpobackendchallenge.service.ApiCallLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiCallLogControllerTest {

    @Mock
    private ApiCallLogService apiCallLogService;

    @InjectMocks
    private ApiCallLogController apiCallLogController;

    private WebTestClient webTestClient;
    private List<ApiCallLog> mockApiCallLogs;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(apiCallLogController).build();

        mockApiCallLogs = List.of(
                new ApiCallLog(1L, LocalDateTime.of(2024, 1, 1, 10, 0), "/test1", "param=1", "response1", 200),
                new ApiCallLog(2L, LocalDateTime.of(2024, 1, 1, 11, 0), "/test2", "param=2", "response2", 201)
        );
    }

    @SuppressWarnings("SequencedCollectionMethodCanBeUsed")
    @Test
    void shouldReturnApiCallLogsSuccessfully() {
        when(apiCallLogService.findAll(0, 10)).thenReturn(Flux.fromIterable(mockApiCallLogs));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api-call-log")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ApiCallLog.class)
                .consumeWith(response -> {
                    List<ApiCallLog> actualLogs = response.getResponseBody();
                    assert actualLogs != null;
                    assert actualLogs.size() == 2;

                    assert actualLogs.get(0).getEndpoint().equals("/test1");
                    assert actualLogs.get(0).getParameters().equals("param=1");
                    assert actualLogs.get(0).getResponse().equals("response1");
                    assert actualLogs.get(0).getHttpStatus() == 200;

                    assert actualLogs.get(1).getEndpoint().equals("/test2");
                    assert actualLogs.get(1).getParameters().equals("param=2");
                    assert actualLogs.get(1).getResponse().equals("response2");
                    assert actualLogs.get(1).getHttpStatus() == 201;
                });

        verify(apiCallLogService, times(1)).findAll(0, 10);
    }

    @Test
    void shouldReturnEmptyListWhenNoLogsExist() {
        when(apiCallLogService.findAll(0, 10)).thenReturn(Flux.empty());

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api-call-log")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ApiCallLog.class)
                .hasSize(0);

        verify(apiCallLogService, times(1)).findAll(0, 10);
    }
}