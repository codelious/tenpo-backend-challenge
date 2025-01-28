package com.tenpo.tenpobackendchallenge.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
public class CalculationRequestDto {
    @NotNull(message = "El num1 no puede ser nulo")
    private Double num1;
    @NotNull(message = "El num2 no puede ser nulo")
    private Double num2;
}
