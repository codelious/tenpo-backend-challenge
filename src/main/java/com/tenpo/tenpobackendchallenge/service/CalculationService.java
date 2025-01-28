package com.tenpo.tenpobackendchallenge.service;

import com.tenpo.tenpobackendchallenge.dto.CalculationResponseDto;
import reactor.core.publisher.Mono;

public interface CalculationService {
    Mono<CalculationResponseDto> calculateWithPercentage(double num1, double num2);
}
