package com.tenpo.tenpobackendchallenge.service;

import com.tenpo.tenpobackendchallenge.config.PercentageApiProperties;
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

    public static final String ERROR_AL_OBTENER_EL_PORCENTAJE_DESPUES_DE_REINTENTAR = "Error al obtener el porcentaje después de reintentar";
    public static final String NO_SE_PUDO_OBTENER_EL_PORCENTAJE_DESDE_EL_SERVICIO_EXTERNO_NI_DESDE_LA_CACHE = "No se pudo obtener el porcentaje desde el servicio externo ni desde la caché.";

    private final WebClient webClient;
    private final PercentageRedisCacheService percentageRedisCacheService;
    private final PercentageApiProperties percentageApiProperties;

    @Autowired
    public PercentageServiceImpl(WebClient.Builder webClientBuilder, PercentageRedisCacheService percentageRedisCacheService, PercentageApiProperties percentageApiProperties) {
        this.percentageApiProperties = percentageApiProperties;
        this.webClient = webClientBuilder.baseUrl(percentageApiProperties.getUrl()).build();
        this.percentageRedisCacheService = percentageRedisCacheService;

        log.info("URL del servicio externo de porcentaje:{}", percentageApiProperties.getUrl());
        log.info("URI del servicio externo de porcentaje:{}", percentageApiProperties.getUri());
    }

    @Override
    public Mono<PercentageResponseDto> getPercentageWithFallback() {
        return fetchPercentageFromExternalService()
                .flatMap(this::cachePercentage)
                .onErrorResume(this::handleErrorWithCache)
                .onErrorMap(this::handleFinalError);
    }

    private Mono<PercentageResponseDto> fetchPercentageFromExternalService() {
        return this.webClient.get().uri(percentageApiProperties.getUri())
                .retrieve()
                .bodyToMono(PercentageResponseDto.class)
                .doOnSubscribe(subscription -> log.info("Llamada al servicio externo iniciada"))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(500))
                        .doBeforeRetry(retrySignal -> log.info("Intento #{}", (retrySignal.totalRetries() + 1)))
                        .onRetryExhaustedThrow((spec, signal) ->
                                new GetPercentageException(ERROR_AL_OBTENER_EL_PORCENTAJE_DESPUES_DE_REINTENTAR)))
                .doOnNext(percentage -> log.info("Respuesta obtenida del servicio externo: {}", percentage));
    }

    private Mono<PercentageResponseDto> cachePercentage(PercentageResponseDto percentage) {
        return percentageRedisCacheService.saveCachedPercentage(Mono.just(percentage))
                .thenReturn(percentage); // Devuelve el porcentaje original después de almacenarlo
    }

    private Mono<PercentageResponseDto> handleErrorWithCache(Throwable error) {
        log.warn("Error al obtener porcentaje desde el servicio externo: {}. Intentando desde caché...", error.getMessage());
        return percentageRedisCacheService.findCachedPercentage();
    }

    private Throwable handleFinalError(Throwable error) {
        log.error(NO_SE_PUDO_OBTENER_EL_PORCENTAJE_DESDE_EL_SERVICIO_EXTERNO_NI_DESDE_LA_CACHE);
        return new GetPercentageException(NO_SE_PUDO_OBTENER_EL_PORCENTAJE_DESDE_EL_SERVICIO_EXTERNO_NI_DESDE_LA_CACHE);
    }

}
