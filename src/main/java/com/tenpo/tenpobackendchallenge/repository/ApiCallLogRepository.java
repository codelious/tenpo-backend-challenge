package com.tenpo.tenpobackendchallenge.repository;

import com.tenpo.tenpobackendchallenge.model.ApiCallLog;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiCallLogRepository extends ReactiveCrudRepository<ApiCallLog, Long> {
}
