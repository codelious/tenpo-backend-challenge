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
    public Flux<ApiCallLog> findAll(int page, int size) {
        // un sistema de paginación básico, la primera pagina es 0
        return apiCallLogRepository.findAll()
                .skip((long) page * size) // salta elementos de paginas anteriores
                .take(size); // toma los elementos necesarios
    }
}
