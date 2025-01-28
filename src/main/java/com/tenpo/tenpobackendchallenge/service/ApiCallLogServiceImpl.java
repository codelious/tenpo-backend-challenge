package com.tenpo.tenpobackendchallenge.service;

import com.tenpo.tenpobackendchallenge.dto.ApiCallLogDto;
import com.tenpo.tenpobackendchallenge.model.ApiCallLog;
import com.tenpo.tenpobackendchallenge.repository.ApiCallLogRepository;
import com.tenpo.tenpobackendchallenge.utils.ApiCallLogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ApiCallLogServiceImpl implements ApiCallLogService {

    private final ApiCallLogRepository apiCallLogRepository;

    @Autowired
    public ApiCallLogServiceImpl(ApiCallLogRepository apiCallLogRepository) {
        this.apiCallLogRepository = apiCallLogRepository;
    }

    @Override
    public Mono<ApiCallLog> create(Mono<ApiCallLogDto> apiCallLogDto) {
        return apiCallLogDto.map(ApiCallLogUtils::toApiCallLog).flatMap(apiCallLogRepository::save);
    }

    @Override
    public Mono<ApiCallLog> findById(Long apiCallLogId) {
        return apiCallLogRepository.findById(apiCallLogId);
    }

    @Override
    public Mono<ApiCallLog> update(Long apiCallLogId, Mono<ApiCallLogDto> apiCallLogDto) {
        return apiCallLogRepository.findById(apiCallLogId)
                .flatMap(apiCallLog -> apiCallLogDto
                        .map(ApiCallLogUtils::toApiCallLog)
                        .doOnNext(a -> a.setId(apiCallLogId)))
                .flatMap(apiCallLogRepository::save);
    }

    @Override
    public Mono<Void> delete(Long apiCallLogId) {
        return apiCallLogRepository.deleteById(apiCallLogId);
    }

    @Override
    public Flux<ApiCallLog> findAll() {
        return apiCallLogRepository.findAll();
    }
}
