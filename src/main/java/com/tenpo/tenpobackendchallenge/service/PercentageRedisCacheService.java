package com.tenpo.tenpobackendchallenge.service;

import com.tenpo.tenpobackendchallenge.dto.PercentageResponseDto;
import reactor.core.publisher.Mono;

public interface PercentageRedisCacheService {
    Mono<PercentageResponseDto> saveCachedPercentage(Mono<PercentageResponseDto> percentageMono);
    Mono<PercentageResponseDto> findCachedPercentage();
}
