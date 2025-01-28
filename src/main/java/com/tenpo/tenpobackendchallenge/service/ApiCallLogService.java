package com.tenpo.tenpobackendchallenge.service;

import com.tenpo.tenpobackendchallenge.dto.ApiCallLogDto;
import com.tenpo.tenpobackendchallenge.model.ApiCallLog;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApiCallLogService {
    Mono<ApiCallLog> create(Mono<ApiCallLogDto> apiCallLogDto);
    Mono<ApiCallLog> findById(Long apiCallLogId);
    Mono<ApiCallLog> update(Long apiCallLogId, Mono<ApiCallLogDto> apiCallLogDto);
    Mono<Void> delete(Long apiCallLogId);
    Flux<ApiCallLog> findAll();
}
