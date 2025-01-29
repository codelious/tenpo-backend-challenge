package com.tenpo.tenpobackendchallenge.service;

import com.tenpo.tenpobackendchallenge.dto.PercentageResponseDto;
import com.tenpo.tenpobackendchallenge.exception.GetPercentageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
public class PercentageRedisCacheServiceImpl implements PercentageRedisCacheService {

    public static final String NO_SE_PUDO_OBTENER_EL_PORCENTAJE_DESDE_REDIS = "No se pudo obtener el porcentaje desde Redis";
    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    private static final String CACHE_KEY = "percentage";

    @Autowired
    public PercentageRedisCacheServiceImpl(ReactiveRedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<PercentageResponseDto> saveCachedPercentage(Mono<PercentageResponseDto> percentageMono) {
        return percentageMono.flatMap(percentage ->
                redisTemplate.opsForValue()
                        .set(CACHE_KEY, percentage, Duration.ofMinutes(30)) // Tiempo de expiración
                        .doOnSuccess(success -> log.info("Porcentaje guardado en Redis: {}", percentage))
                        .thenReturn(percentage)
        );
    }

    @Override
    public Mono<PercentageResponseDto> findCachedPercentage() {
        return redisTemplate.opsForValue()
                .get(CACHE_KEY)
                .cast(PercentageResponseDto.class)
                .switchIfEmpty(Mono.error(new GetPercentageException(NO_SE_PUDO_OBTENER_EL_PORCENTAJE_DESDE_REDIS)))
                .doOnNext(cachedPercentage -> log.info("Porcentaje recuperado de Redis: {}", cachedPercentage));
    }
}
