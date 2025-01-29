package com.tenpo.tenpobackendchallenge.components;

import com.tenpo.tenpobackendchallenge.dto.ApiCallLogDto;
import com.tenpo.tenpobackendchallenge.service.ApiCallLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked", "FieldCanBeLocal"})
class ApiCallLoggingFilterTest {

    private ApiCallLogService apiCallLogService;
    private ApiCallLoggingFilter apiCallLoggingFilter;
    private ServerWebExchange exchange;
    private WebFilterChain chain;
    private ServerHttpRequest request;
    private ServerHttpResponse response;
    private ServerWebExchange.Builder exchangeBuilder;

    @BeforeEach
    void setUp() {
        apiCallLogService = mock(ApiCallLogService.class);
        apiCallLoggingFilter = new ApiCallLoggingFilter(apiCallLogService);

        exchange = mock(ServerWebExchange.class);
        chain = mock(WebFilterChain.class);
        request = mock(ServerHttpRequest.class);
        response = mock(ServerHttpResponse.class);
        exchangeBuilder = mock(ServerWebExchange.Builder.class);

        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        when(chain.filter(exchange)).thenReturn(Mono.empty()); // Simula que el request sigue normalmente

        // Configurar un RequestPath válido y un URI válido
        RequestPath requestPath = RequestPath.parse(URI.create("http://localhost:8080/api/test"), "");
        when(request.getPath()).thenReturn(requestPath);
        when(request.getURI()).thenReturn(URI.create("http://localhost:8080/api/test"));

        // Configurar el builder de mutate() para evitar el error
        when(exchange.mutate()).thenReturn(exchangeBuilder);
        when(exchangeBuilder.response(any(ServerHttpResponse.class))).thenReturn(exchangeBuilder);
        when(exchangeBuilder.build()).thenReturn(exchange);
    }

    @Test
    void shouldExcludeSwaggerEndpoints() {
        RequestPath swaggerPath = RequestPath.parse(URI.create("http://localhost:8080/swagger-ui.html"), "");
        when(request.getPath()).thenReturn(swaggerPath);
        when(request.getURI()).thenReturn(URI.create("http://localhost:8080/swagger-ui.html")); // Asegurar URI válido

        Mono<Void> result = apiCallLoggingFilter.filter(exchange, chain);

        verify(apiCallLogService, never()).create(any(Mono.class)); // No debe registrar logs para Swagger
        assertDoesNotThrow(() -> result.block()); // No debe lanzar errores
    }

    @Test
    void shouldLogApiCallSuccessfully() {
        RequestPath apiPath = RequestPath.parse(URI.create("http://localhost:8080/api/test"), "");
        when(request.getPath()).thenReturn(apiPath);
        when(request.getURI()).thenReturn(URI.create("http://localhost:8080/api/test"));
        when(request.getQueryParams()).thenReturn(mock(org.springframework.util.MultiValueMap.class));
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);
        when(apiCallLogService.create(any(Mono.class))).thenReturn(Mono.empty());

        Mono<Void> result = apiCallLoggingFilter.filter(exchange, chain);
        result.block(); // Ejecutar la lógica de filtro

        ArgumentCaptor<Mono<ApiCallLogDto>> logCaptor = ArgumentCaptor.forClass(Mono.class);
        verify(apiCallLogService).create(logCaptor.capture());

        ApiCallLogDto log = logCaptor.getValue().block();
        assertNotNull(log);
        assertEquals("http://localhost:8080/api/test", log.getEndpoint());
        assertEquals(200, log.getHttpStatus());
    }

    @Test
    void shouldHandleErrorGracefully() {
        RequestPath errorPath = RequestPath.parse(URI.create("http://localhost:8080/api/test-error"), "");
        when(request.getPath()).thenReturn(errorPath);
        when(request.getURI()).thenReturn(URI.create("http://localhost:8080/api/test-error"));
        when(request.getQueryParams()).thenReturn(mock(org.springframework.util.MultiValueMap.class));
        when(response.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        when(apiCallLogService.create(any(Mono.class))).thenReturn(Mono.empty());

        Mono<Void> result = apiCallLoggingFilter.filter(exchange, chain);
        result.block(); // Ejecutar la lógica de filtro

        ArgumentCaptor<Mono<ApiCallLogDto>> logCaptor = ArgumentCaptor.forClass(Mono.class);
        verify(apiCallLogService).create(logCaptor.capture());

        ApiCallLogDto log = logCaptor.getValue().block();
        assertNotNull(log);
        assertEquals("http://localhost:8080/api/test-error", log.getEndpoint());
        assertEquals(500, log.getHttpStatus());
    }
}