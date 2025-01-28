package com.tenpo.tenpobackendchallenge.service;

import com.tenpo.tenpobackendchallenge.dto.PercentageResponseDto;
import com.tenpo.tenpobackendchallenge.exception.GetPercentageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@Slf4j
public class PercentageServiceImpl implements PercentageService {

    private final WebClient webClient;
    private final PercentageCacheService percentageCacheService;

    @Autowired
    public PercentageServiceImpl(WebClient.Builder webClientBuilder, PercentageCacheService percentageCacheService) {
        this.webClient = webClientBuilder.baseUrl("https://42c939fb-7574-4341-91ca-b59c0ed06ddb.mock.pstmn.io").build();
        this.percentageCacheService = percentageCacheService;
    }

    @Override
    public Mono<PercentageResponseDto> getPercentageWithFallback() {
        return fetchPercentageFromExternalService()
                .flatMap(this::cachePercentage)
                .onErrorResume(this::handleErrorWithCache)
                .onErrorMap(this::handleFinalError);
    }

    private Mono<PercentageResponseDto> fetchPercentageFromExternalService() {
        return this.webClient.get().uri("/percentage")
                .retrieve()
                .bodyToMono(PercentageResponseDto.class)
                .doOnSubscribe(subscription -> log.info("Llamada al servicio externo iniciada"))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(500))
                        .doBeforeRetry(retrySignal -> log.info("Reintentando... intento #{}", (retrySignal.totalRetries() + 1)))
                        .onRetryExhaustedThrow((spec, signal) ->
                                new GetPercentageException("Error al obtener el porcentaje después de reintentar")))
                .doOnNext(percentage -> log.info("Respuesta obtenida del servicio externo: {}", percentage));
    }

    private Mono<PercentageResponseDto> cachePercentage(PercentageResponseDto percentage) {
        return percentageCacheService.saveCachedPercentage(Mono.just(percentage))
                .thenReturn(percentage); // Devuelve el porcentaje original tras almacenarlo
    }

    private Mono<PercentageResponseDto> handleErrorWithCache(Throwable error) {
        log.warn("Error al obtener porcentaje desde el servicio externo: {}. Intentando desde caché...", error.getMessage());
        return percentageCacheService.findCachedPercentage();
    }

    private Throwable handleFinalError(Throwable error) {
        log.error("No se pudo obtener el porcentaje desde el servicio externo ni desde la caché.");
        return new GetPercentageException("No se pudo obtener el porcentaje desde el servicio externo ni desde la caché.");
    }

}
