package com.tenpo.tenpobackendchallenge.service;

import com.tenpo.tenpobackendchallenge.dto.PercentageResponseDto;
import reactor.core.publisher.Mono;

public interface PercentageService {
    Mono<PercentageResponseDto> getPercentageWithFallback();
}
