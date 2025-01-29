package com.tenpo.tenpobackendchallenge.controller;

import com.tenpo.tenpobackendchallenge.dto.CalculationResponseDto;
import com.tenpo.tenpobackendchallenge.service.CalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculationControllerTest {

    @Mock
    private CalculationService calculationService;

    @InjectMocks
    private CalculationController calculationController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(calculationController).build();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void shouldCalculateWithDynamicPercentageSuccessfully() {
        double num1 = 50.0;
        double num2 = 50.0;
        CalculationResponseDto responseDto = new CalculationResponseDto(110.0); // (50 + 50) + 10% = 110

        when(calculationService.calculateWithPercentage(num1, num2)).thenReturn(Mono.just(responseDto));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/calculation")
                        .queryParam("num1", num1)
                        .queryParam("num2", num2)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(CalculationResponseDto.class)
                .value(response -> response.getSum().equals(responseDto.getSum()));

        verify(calculationService, times(1)).calculateWithPercentage(num1, num2);
    }

    @Test
    void shouldReturnErrorWhenCalculationServiceFails() {
        double num1 = 30.0;
        double num2 = 20.0;

        when(calculationService.calculateWithPercentage(num1, num2)).thenReturn(Mono.error(new RuntimeException("Error en el servicio")));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/calculation")
                        .queryParam("num1", num1)
                        .queryParam("num2", num2)
                        .build())
                .exchange()
                .expectStatus().is5xxServerError();

        verify(calculationService, times(1)).calculateWithPercentage(num1, num2);
    }

    @Test
    void shouldHandleInvalidParameters() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/calculation")
                        .queryParam("num1", "abc")  // Valor inválido
                        .queryParam("num2", "xyz")  // Valor inválido
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }
}