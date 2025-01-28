package com.tenpo.tenpobackendchallenge.controller;

import com.tenpo.tenpobackendchallenge.dto.ApiCallLogDto;
import com.tenpo.tenpobackendchallenge.model.ApiCallLog;
import com.tenpo.tenpobackendchallenge.service.ApiCallLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api-call-log")
public class ApiCallLogController {

    private final ApiCallLogService apiCallLogService;

    @Autowired
    public ApiCallLogController(ApiCallLogService apiCallLogService) {
        this.apiCallLogService = apiCallLogService;
    }

    @PostMapping
    Mono<ApiCallLog> create(@RequestBody Mono<ApiCallLogDto> apiCallLogDto) {
        return apiCallLogService.create(apiCallLogDto);
    }

    @GetMapping("/{apiCallLogId}")
    Mono<ResponseEntity<ApiCallLog>> retrieve(@PathVariable Long apiCallLogId) {
        return apiCallLogService.findById(apiCallLogId)
                .map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{apiCallLogId}")
    Mono<ResponseEntity<ApiCallLog>> update(@PathVariable Long apiCallLogId, @RequestBody Mono<ApiCallLogDto> apiCallLogDto) {
        return apiCallLogService.update(apiCallLogId, apiCallLogDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{apiCallLogId}")
    Mono<Void> delete(@PathVariable Long apiCallLogId) {
        return apiCallLogService.delete(apiCallLogId);
    }

    @GetMapping
    Flux<ApiCallLog> findAll() {
        return apiCallLogService.findAll();
    }
}
