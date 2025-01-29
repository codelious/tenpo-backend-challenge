package com.tenpo.tenpobackendchallenge.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.InetSocketAddress;
import java.time.Duration;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitingFilterTest {

    @Mock
    private ReactiveRedisTemplate<String, Object> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, Object> valueOperations;

    @Mock
    private WebFilterChain webFilterChain;

    @InjectMocks
    private RateLimitingFilter rateLimitingFilter;

    private MockServerWebExchange exchange;

    @BeforeEach
    void setUp() {
        // when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @SuppressWarnings("SameParameterValue")
    private MockServerWebExchange createMockExchange(String path, String clientIp) {
        return MockServerWebExchange.builder(MockServerHttpRequest.get(path)
                        .remoteAddress(new InetSocketAddress(clientIp, 8080))) // Mockeamos dirección IP
                .build();
    }

    @Test
    void shouldAllowRequestWithinRateLimit() {
        exchange = createMockExchange("/calculation", "192.168.1.100");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(Mono.just(1L));
        when(redisTemplate.expire(anyString(), any(Duration.class))).thenReturn(Mono.just(true));
        when(webFilterChain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = rateLimitingFilter.filter(exchange, webFilterChain);

        StepVerifier.create(result)
                .verifyComplete();

        verify(valueOperations, times(1)).increment(anyString());
        verify(redisTemplate, times(1)).expire(anyString(), any(Duration.class));
        verify(webFilterChain, times(1)).filter(exchange);
    }

    @Test
    void shouldRejectRequestWhenRateLimitExceeded() {
        exchange = createMockExchange("/calculation", "192.168.1.100");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(Mono.just(4L)); // Excediendo el límite

        Mono<Void> result = rateLimitingFilter.filter(exchange, webFilterChain);

        StepVerifier.create(result)
                .expectComplete()
                .verify();

        MockServerHttpResponse response = exchange.getResponse();
        assert response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS;

        verify(valueOperations, times(1)).increment(anyString());
        verify(webFilterChain, never()).filter(exchange);
    }

    @Test
    void shouldExcludeSwaggerEndpoints() {
        exchange = createMockExchange("/swagger-ui.html", "192.168.1.100");
        when(webFilterChain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = rateLimitingFilter.filter(exchange, webFilterChain);

        StepVerifier.create(result)
                .verifyComplete();

        verify(valueOperations, never()).increment(anyString());
        verify(webFilterChain, times(1)).filter(exchange);
    }
}