package com.tenpo.tenpobackendchallenge.service;

import com.tenpo.tenpobackendchallenge.dto.PercentageResponseDto;
import com.tenpo.tenpobackendchallenge.exception.GetPercentageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PercentageServiceImplTest {

    @Mock
    private WebClient.Builder webClientBuilderMock;

    @Mock
    private WebClient webClientMock;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @Mock
    private PercentageRedisCacheService percentageRedisCacheService;

    // @InjectMocks
    private PercentageServiceImpl percentageService;

    private PercentageResponseDto percentageResponse;

    @BeforeEach
    void setUp() {
        percentageResponse = new PercentageResponseDto(10.5);

        // Simulación de WebClient.Builder para evitar el NullPointerException
        when(webClientBuilderMock.baseUrl(anyString())).thenReturn(webClientBuilderMock);
        when(webClientBuilderMock.build()).thenReturn(webClientMock);

        // Simulación del comportamiento del WebClient
        when(webClientMock.get()).thenAnswer(invocation -> requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenAnswer(invocation -> requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.retrieve()).thenReturn(responseSpecMock);

        // Instanciar manualmente el servicio con el WebClient mockeado
        percentageService = new PercentageServiceImpl(webClientBuilderMock, percentageRedisCacheService);
    }

    @Test
    void shouldGetPercentageSuccessfully() {
        when(responseSpecMock.bodyToMono(PercentageResponseDto.class)).thenReturn(Mono.just(percentageResponse));
        when(percentageRedisCacheService.saveCachedPercentage(any())).thenReturn(Mono.just(percentageResponse));

        Mono<PercentageResponseDto> result = percentageService.getPercentageWithFallback();

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getPercentage() == 10.5)
                .verifyComplete();

        verify(percentageRedisCacheService, times(1)).saveCachedPercentage(any());
    }

    @Test
    void shouldUseCacheWhenExternalServiceFails() {
        when(responseSpecMock.bodyToMono(PercentageResponseDto.class)).thenReturn(Mono.error(new WebClientResponseException(500, "Internal Server Error", null, null, null)));
        when(percentageRedisCacheService.findCachedPercentage()).thenReturn(Mono.just(percentageResponse));

        Mono<PercentageResponseDto> result = percentageService.getPercentageWithFallback();

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getPercentage() == 10.5)
                .verifyComplete();

        verify(percentageRedisCacheService, times(1)).findCachedPercentage();
    }

    @Test
    void shouldThrowExceptionWhenExternalServiceAndCacheFail() {
        when(responseSpecMock.bodyToMono(PercentageResponseDto.class)).thenReturn(Mono.error(new WebClientResponseException(500, "Internal Server Error", null, null, null)));
        when(percentageRedisCacheService.findCachedPercentage()).thenReturn(Mono.error(new GetPercentageException("No se pudo obtener el porcentaje desde Redis")));

        Mono<PercentageResponseDto> result = percentageService.getPercentageWithFallback();

        StepVerifier.create(result)
                .expectError(GetPercentageException.class)
                .verify();

        verify(percentageRedisCacheService, times(1)).findCachedPercentage();
    }
}