package com.tenpo.tenpobackendchallenge.service;

import com.tenpo.tenpobackendchallenge.dto.PercentageResponseDto;
import reactor.core.publisher.Mono;

public interface PercentageCacheService {
    Mono<PercentageResponseDto> saveCachedPercentage(Mono<PercentageResponseDto> percentageMono);
    Mono<PercentageResponseDto> findCachedPercentage();
}
