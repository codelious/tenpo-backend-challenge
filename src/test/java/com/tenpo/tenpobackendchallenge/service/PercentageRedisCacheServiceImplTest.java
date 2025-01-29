package com.tenpo.tenpobackendchallenge.service;

import com.tenpo.tenpobackendchallenge.dto.PercentageResponseDto;
import com.tenpo.tenpobackendchallenge.exception.GetPercentageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PercentageRedisCacheServiceImplTest {

    @Mock
    private ReactiveRedisTemplate<String, Object> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, Object> valueOperations;

    @InjectMocks
    private PercentageRedisCacheServiceImpl percentageRedisCacheService;

    private static final String CACHE_KEY = "percentage";
    private PercentageResponseDto percentageResponseDto;

    @BeforeEach
    void setUp() {
        percentageResponseDto = new PercentageResponseDto(10.5);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void shouldSaveCachedPercentage() {
        when(valueOperations.set(eq(CACHE_KEY), any(PercentageResponseDto.class), any(Duration.class)))
                .thenReturn(Mono.just(true));

        Mono<PercentageResponseDto> result = percentageRedisCacheService.saveCachedPercentage(Mono.just(percentageResponseDto));

        StepVerifier.create(result)
                .expectNextMatches(savedPercentage -> savedPercentage.getPercentage() == 10.5)
                .verifyComplete();

        verify(valueOperations, times(1)).set(eq(CACHE_KEY), eq(percentageResponseDto), any(Duration.class));
    }

    @Test
    void shouldFindCachedPercentage() {
        when(valueOperations.get(CACHE_KEY)).thenReturn(Mono.just(percentageResponseDto));

        Mono<PercentageResponseDto> result = percentageRedisCacheService.findCachedPercentage();

        StepVerifier.create(result)
                .expectNextMatches(cachedPercentage -> cachedPercentage.getPercentage() == 10.5)
                .verifyComplete();

        verify(valueOperations, times(1)).get(CACHE_KEY);
    }

    @Test
    void shouldReturnErrorWhenCacheIsEmpty() {
        when(valueOperations.get(CACHE_KEY)).thenReturn(Mono.empty());

        Mono<PercentageResponseDto> result = percentageRedisCacheService.findCachedPercentage();

        StepVerifier.create(result)
                .expectError(GetPercentageException.class)
                .verify();

        verify(valueOperations, times(1)).get(CACHE_KEY);
    }
}