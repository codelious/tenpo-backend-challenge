package com.tenpo.tenpobackendchallenge.controller;

import com.tenpo.tenpobackendchallenge.dto.CalculationResponseDto;
import com.tenpo.tenpobackendchallenge.service.CalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("calculation")
@Tag(name = "Calculation", description = "Endpoints para realizar cálculos con porcentaje dinámico")
public class CalculationController {

    private final CalculationService calculationService;

    @Autowired
    public CalculationController(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    @Operation(summary = "Calcula una suma con un porcentaje dinámico",
            description = "Suma los dos números proporcionados y les aplica un porcentaje dinámico obtenido de un servicio externo.")
    @GetMapping
    public Mono<CalculationResponseDto> calculateWithDynamicPercentage(
            @Parameter(description = "Primer número de la suma") @RequestParam double num1,
            @Parameter(description = "Segundo número de la suma") @RequestParam double num2) {
        return calculationService.calculateWithPercentage(num1, num2);
    }

}
