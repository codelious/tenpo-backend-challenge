package com.tenpo.tenpobackendchallenge.controller;

import com.tenpo.tenpobackendchallenge.model.ApiCallLog;
import com.tenpo.tenpobackendchallenge.service.ApiCallLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("api-call-log")
@Tag(name = "API Call Log", description = "Endpoints para consultar los registros de llamadas a la API")
public class ApiCallLogController {

    private final ApiCallLogService apiCallLogService;

    @Autowired
    public ApiCallLogController(ApiCallLogService apiCallLogService) {
        this.apiCallLogService = apiCallLogService;
    }

    @Operation(summary = "Obtiene registros de llamadas a la API",
            description = "Retorna un listado de los registros de llamadas realizadas a la API con paginación.")
    @GetMapping
    Flux<ApiCallLog> findAll(@Parameter(description = "Número de la página (comienza en 0)", example = "0") @RequestParam(defaultValue = "0") int page,
                             @Parameter(description = "Cantidad de registros por página", example = "10") @RequestParam(defaultValue = "10") int size) {
        return apiCallLogService.findAll(page, size);
    }
}
