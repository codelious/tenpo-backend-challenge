package com.tenpo.tenpobackendchallenge.controller;

import com.tenpo.tenpobackendchallenge.dto.CalculationRequestDto;
import com.tenpo.tenpobackendchallenge.dto.CalculationResponseDto;
import com.tenpo.tenpobackendchallenge.service.CalculationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("calculation")
public class CalculationController {

    private final CalculationService calculationService;

    @Autowired
    public CalculationController(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    @GetMapping
    public Mono<CalculationResponseDto> calculateWithDynamicPercentage(@RequestParam double num1, @RequestParam double num2) {
        return calculationService.calculateWithPercentage(num1, num2);
    }

}
