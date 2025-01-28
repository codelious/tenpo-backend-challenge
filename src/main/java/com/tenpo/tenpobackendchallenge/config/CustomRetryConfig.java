package com.tenpo.tenpobackendchallenge.config;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;


@Configuration
@Slf4j
public class CustomRetryConfig {

//    @Bean
//    public Retry percentageRetry() {
//        Retry retry = Retry.of("percentageRetry", RetryConfig.custom()
//                .maxAttempts(3) // 3 intentos maximo
//                .waitDuration(Duration.ofMillis(500)) // 500 ms entre cada intento
//                .retryExceptions(RuntimeException.class)
//                .build());
//        retry.getEventPublisher()
//                .onRetry(event -> log.info("Intento # {}", event.getNumberOfRetryAttempts()))
//                .onSuccess(event -> log.info("Llamada exitosa despues de {} intentos", event.getNumberOfRetryAttempts()))
//                .onError(event -> log.info("Error despues de {} intentos", event.getNumberOfRetryAttempts()));
//        return retry;
//    }
}
