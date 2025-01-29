package com.tenpo.tenpobackendchallenge.components;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class RateLimitingFilter implements WebFilter {

    public static final String TOO_MANY_REQUESTS_LIMITE_DE_3_SOLICITUDES_POR_MINUTO_ALCANZADO = "Too Many Requests: Límite de 3 solicitudes por minuto alcanzado.";
    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    private static final String RATE_LIMIT_KEY_PREFIX = "rate_limit:";
    private static final int MAX_REQUESTS_PER_MINUTE = 3;

    private static final List<String> SWAGGER_WHITELIST = List.of(
            "/swagger-ui.html",
            "/swagger-ui/index.html",
            "/swagger-ui/",
            "/v3/api-docs",
            "/v3/api-docs.yaml",
            "/webjars/"
    );

    @Autowired
    public RateLimitingFilter(ReactiveRedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @NotNull
    @Override
    public Mono<Void> filter(@NotNull ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Excluir Swagger del Rate Limiting
        if (isSwaggerEndpoint(path)) {
            return chain.filter(exchange);
        }

        String clientId = getClientIdentifier(exchange); // Identificar al cliente (por IP o header específico)
        String redisKey = RATE_LIMIT_KEY_PREFIX + clientId;

        return redisTemplate.opsForValue()
                .increment(redisKey) // Incrementar el contador en Redis
                .flatMap(requestCount -> {
                    if (requestCount == 1) {
                        // Configurar la expiración solo en el primer acceso como parte del flujo
                        return redisTemplate.expire(redisKey, Duration.ofMinutes(1))
                                .then(chain.filter(exchange)); // Encadenar y continuar el flujo
                    }
                    if (requestCount > MAX_REQUESTS_PER_MINUTE) {
                        log.warn("Rate limit excedido para el cliente: {}", clientId);
                        return handleRateLimitExceeded(exchange); // Manejar el límite excedido
                    }
                    return chain.filter(exchange); // Continuar con la solicitud si está dentro del límite
                });
    }

    private boolean isSwaggerEndpoint(String path) {
        return SWAGGER_WHITELIST.stream().anyMatch(path::startsWith);
    }

    private String getClientIdentifier(ServerWebExchange exchange) {
        // Usa la dirección IP como identificador. Esto se podría cambiar para usar otro identificador
        return Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress();
    }

    private Mono<Void> handleRateLimitExceeded(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS); // 429 Too Many Requests
        byte[] bytes = TOO_MANY_REQUESTS_LIMITE_DE_3_SOLICITUDES_POR_MINUTO_ALCANZADO.getBytes();
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }
}
