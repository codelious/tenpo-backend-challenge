package com.tenpo.tenpobackendchallenge.service;

import com.tenpo.tenpobackendchallenge.dto.ApiCallLogDto;
import com.tenpo.tenpobackendchallenge.model.ApiCallLog;
import com.tenpo.tenpobackendchallenge.repository.ApiCallLogRepository;
import com.tenpo.tenpobackendchallenge.utils.ApiCallLogUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiCallLogServiceImplTest {

    @Mock
    private ApiCallLogRepository apiCallLogRepository;

    @InjectMocks
    private ApiCallLogServiceImpl apiCallLogService;

    private ApiCallLogDto apiCallLogDto;
    private ApiCallLog apiCallLog;

    @BeforeEach
    void setUp() {
        apiCallLogDto = new ApiCallLogDto(LocalDateTime.now(), "/test", "param=1", "OK", 200);
        apiCallLog = ApiCallLogUtils.toApiCallLog(apiCallLogDto);
    }

    @Test
    void shouldCreateApiCallLog() {
        when(apiCallLogRepository.save(any(ApiCallLog.class))).thenReturn(Mono.just(apiCallLog));

        Mono<ApiCallLog> result = apiCallLogService.create(Mono.just(apiCallLogDto));

        StepVerifier.create(result)
                .expectNextMatches(savedLog ->
                        savedLog.getEndpoint().equals(apiCallLog.getEndpoint()) &&
                                savedLog.getParameters().equals(apiCallLog.getParameters()) &&
                                savedLog.getResponse().equals(apiCallLog.getResponse()) &&
                                savedLog.getHttpStatus() == apiCallLog.getHttpStatus()
                )
                .verifyComplete();

        verify(apiCallLogRepository, times(1)).save(any(ApiCallLog.class));
    }

    @Test
    void shouldReturnPagedResultsWhenFindingAll() {
        List<ApiCallLog> logs = List.of(apiCallLog, apiCallLog, apiCallLog);
        when(apiCallLogRepository.findAll()).thenReturn(Flux.fromIterable(logs));

        Flux<ApiCallLog> result = apiCallLogService.findAll(0, 2); // Paginación: página 0, tamaño 2

        StepVerifier.create(result)
                .expectNextCount(2) // Solo debe devolver 2 elementos
                .verifyComplete();

        verify(apiCallLogRepository, times(1)).findAll();
    }

    @Test
    void shouldHandleErrorWhenCreateFails() {
        when(apiCallLogRepository.save(any(ApiCallLog.class))).thenReturn(Mono.error(new RuntimeException("Database error")));

        Mono<ApiCallLog> result = apiCallLogService.create(Mono.just(apiCallLogDto));

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().equals("Database error"))
                .verify();

        verify(apiCallLogRepository, times(1)).save(any(ApiCallLog.class));
    }

    @Test
    void shouldHandleErrorWhenFindAllFails() {
        when(apiCallLogRepository.findAll()).thenReturn(Flux.error(new RuntimeException("Database error")));

        Flux<ApiCallLog> result = apiCallLogService.findAll(0, 2);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().equals("Database error"))
                .verify();

        verify(apiCallLogRepository, times(1)).findAll();
    }
}