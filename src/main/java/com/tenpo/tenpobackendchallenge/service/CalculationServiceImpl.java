package com.tenpo.tenpobackendchallenge.service;

import com.tenpo.tenpobackendchallenge.dto.CalculationResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CalculationServiceImpl implements CalculationService {

    private final PercentageService percentageService;

    @Autowired
    public CalculationServiceImpl(PercentageService percentageService) {
        this.percentageService = percentageService;
    }

    @Override
    public Mono<CalculationResponseDto> calculateWithPercentage(double num1, double num2) {
        return percentageService.getPercentageWithFallback()
                .map(percentageResponseDto -> {
                    double sum = num1 + num2;
                    return new CalculationResponseDto(sum + (sum * (percentageResponseDto.getPercentage() / 100)));
                });
    }
}
