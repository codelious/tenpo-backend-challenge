package com.tenpo.tenpobackendchallenge.components;

import com.tenpo.tenpobackendchallenge.dto.ApiCallLogDto;
import com.tenpo.tenpobackendchallenge.service.ApiCallLogService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class ApiCallLoggingFilter implements WebFilter {

    private static final int MAX_RESPONSE_LENGTH = 1000;
    private final ApiCallLogService apiCallLogService;

    private static final List<String> SWAGGER_WHITELIST = List.of(
            "/swagger-ui.html",
            "/swagger-ui/",
            "/swagger-ui/index.html",
            "/v3/api-docs",
            "/v3/api-docs.yaml",
            "/v3/api-docs/swagger-config",
            "/webjars/"
    );

    @Autowired
    public ApiCallLoggingFilter(ApiCallLogService apiCallLogService) {
        this.apiCallLogService = apiCallLogService;
    }

    @NotNull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // Excluir Swagger de la generación de logs
        if (isSwaggerEndpoint(path)) {
            return chain.filter(exchange);
        }

        LocalDateTime timestamp = LocalDateTime.now();
        String endpoint = request.getURI().toString();
        String parameters = request.getQueryParams().toString();

        ServerHttpResponse originalResponse = exchange.getResponse();
        StringBuilder capturedBody = new StringBuilder();

        ServerHttpResponse decoratedResponse = getDecoratedResponse(originalResponse, capturedBody);

        // Continuar con la cadena y registrar los logs
        return chain.filter(exchange.mutate().response(decoratedResponse).build())
                .doOnSuccess(done -> logRequest(exchange, timestamp, endpoint, parameters, capturedBody.toString()))
                .doOnError(throwable -> logRequest(exchange, timestamp, endpoint, parameters, capturedBody.toString()));
    }

    private boolean isSwaggerEndpoint(String path) {
        return SWAGGER_WHITELIST.stream().anyMatch(path::startsWith);
    }

    @NotNull
    private ServerHttpResponse getDecoratedResponse(ServerHttpResponse originalResponse, StringBuilder capturedBody) {
        // Decorar la respuesta para capturar el body
        // Si no es Flux ni Mono, procesar normalmente
        return new ServerHttpResponseDecorator(originalResponse) {
            @NotNull
            @Override
            public Mono<Void> writeWith(@NotNull Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    // Procesar cuerpo como Flux
                    return super.writeWith(processFluxBody((Flux<? extends DataBuffer>) body, capturedBody));
                } else if (body instanceof Mono) {
                    // Procesar cuerpo como Mono
                    return super.writeWith(processMonoBody((Mono<? extends DataBuffer>) body, capturedBody));
                }
                return super.writeWith(body); // Si no es Flux ni Mono, procesar normalmente
            }
        };
    }

    private Flux<DataBuffer> processFluxBody(Flux<? extends DataBuffer> fluxBody, StringBuilder capturedBody) {
        return fluxBody.map(dataBuffer -> processDataBuffer(dataBuffer, capturedBody));
    }

    private Mono<DataBuffer> processMonoBody(Mono<? extends DataBuffer> monoBody, StringBuilder capturedBody) {
        return monoBody.map(dataBuffer -> processDataBuffer(dataBuffer, capturedBody));
    }

    private DataBuffer processDataBuffer(DataBuffer dataBuffer, StringBuilder capturedBody) {
        // Leer contenido del buffer
        byte[] content = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(content);
        DataBufferUtils.release(dataBuffer); // Liberar el buffer original

        String chunk = new String(content, StandardCharsets.UTF_8);

        // Truncar el contenido si es necesario
        if (capturedBody.length() + chunk.length() <= MAX_RESPONSE_LENGTH) {
            capturedBody.append(chunk);
        } else if (capturedBody.length() < MAX_RESPONSE_LENGTH) {
            int remainingSpace = MAX_RESPONSE_LENGTH - capturedBody.length();
            capturedBody.append(chunk, 0, remainingSpace).append("...");
        }

        // Retornar un nuevo DataBuffer para continuar la cadena
        return dataBuffer.factory().wrap(content);
    }

    private void logRequest(ServerWebExchange exchange, LocalDateTime timestamp, String endpoint, String parameters, String responseBody) {
        ServerHttpResponse response = exchange.getResponse();
        int httpStatus = response.getStatusCode() != null ? response.getStatusCode().value() : 500;

        ApiCallLogDto logDto = new ApiCallLogDto();
        logDto.setTimestamp(timestamp);
        logDto.setEndpoint(endpoint);
        logDto.setParameters(parameters);
        logDto.setHttpStatus(httpStatus);
        logDto.setResponse(responseBody);

        apiCallLogService.create(Mono.just(logDto))
                .doOnSuccess(apiCallLog -> log.info("Log registrado exitosamente para el endpoint: {}", endpoint))
                .doOnError(error -> log.error("Error al registrar log para el endpoint {} : {}", endpoint, error.getMessage()))
                .subscribe();
    }
}