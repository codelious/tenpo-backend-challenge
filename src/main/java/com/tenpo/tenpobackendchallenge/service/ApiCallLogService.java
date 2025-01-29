package com.tenpo.tenpobackendchallenge.service;

import com.tenpo.tenpobackendchallenge.dto.ApiCallLogDto;
import com.tenpo.tenpobackendchallenge.model.ApiCallLog;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApiCallLogService {
    Mono<ApiCallLog> create(Mono<ApiCallLogDto> apiCallLogDto);
    Flux<ApiCallLog> findAll(int page, int size);
}
