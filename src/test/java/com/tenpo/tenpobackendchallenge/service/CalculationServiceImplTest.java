package com.tenpo.tenpobackendchallenge.service;

import com.tenpo.tenpobackendchallenge.dto.CalculationResponseDto;
import com.tenpo.tenpobackendchallenge.dto.PercentageResponseDto;
import com.tenpo.tenpobackendchallenge.exception.GetPercentageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculationServiceImplTest {

    @Mock
    private PercentageService percentageService;

    @InjectMocks
    private CalculationServiceImpl calculationService;

    private PercentageResponseDto percentageResponse;

    @BeforeEach
    void setUp() {
        percentageResponse = new PercentageResponseDto(10.0); // Simulación de 10% de incremento
    }

    @Test
    void shouldCalculateWithPercentageSuccessfully() {
        when(percentageService.getPercentageWithFallback()).thenReturn(Mono.just(percentageResponse));

        Mono<CalculationResponseDto> result = calculationService.calculateWithPercentage(100, 50);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getSum() == 165.0) // (100 + 50) + 10% = 165
                .verifyComplete();

        verify(percentageService, times(1)).getPercentageWithFallback();
    }

    @Test
    void shouldHandleZeroPercentage() {
        when(percentageService.getPercentageWithFallback()).thenReturn(Mono.just(new PercentageResponseDto(0.0)));

        Mono<CalculationResponseDto> result = calculationService.calculateWithPercentage(80, 20);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getSum() == 100.0) // (80 + 20) + 0% = 100
                .verifyComplete();

        verify(percentageService, times(1)).getPercentageWithFallback();
    }

    @Test
    void shouldHandleNegativePercentage() {
        when(percentageService.getPercentageWithFallback()).thenReturn(Mono.just(new PercentageResponseDto(-10.0)));

        Mono<CalculationResponseDto> result = calculationService.calculateWithPercentage(200, 100);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getSum() == 270.0) // (200 + 100) - 10% = 270
                .verifyComplete();

        verify(percentageService, times(1)).getPercentageWithFallback();
    }

    @Test
    void shouldReturnErrorWhenPercentageServiceFails() {
        when(percentageService.getPercentageWithFallback()).thenReturn(Mono.error(new GetPercentageException("Servicio no disponible")));

        Mono<CalculationResponseDto> result = calculationService.calculateWithPercentage(10, 10);

        StepVerifier.create(result)
                .expectError(GetPercentageException.class)
                .verify();

        verify(percentageService, times(1)).getPercentageWithFallback();
    }
}