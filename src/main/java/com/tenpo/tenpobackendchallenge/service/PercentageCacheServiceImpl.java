package com.tenpo.tenpobackendchallenge.service;

import com.tenpo.tenpobackendchallenge.dto.PercentageResponseDto;
import com.tenpo.tenpobackendchallenge.exception.GetPercentageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class PercentageCacheServiceImpl implements PercentageCacheService {

    private final CacheManager cacheManager;

    @Autowired
    public PercentageCacheServiceImpl(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }


    @Override
    public Mono<PercentageResponseDto> saveCachedPercentage(Mono<PercentageResponseDto> percentageMono) {
        return percentageMono.doOnNext(percentage -> {
            Cache cache = cacheManager.getCache("percentageCache");
            if (cache != null) {
                log.info("Guardando porcentaje en cache: {}", percentage);
                cache.put("percentage", percentage);
            }
        });
    }

    @Override
    public Mono<PercentageResponseDto> findCachedPercentage() {
        Cache cache = cacheManager.getCache("percentageCache");
        if (cache != null) {
            PercentageResponseDto cachedPercentage = cache.get("percentage", PercentageResponseDto.class);
            if (cachedPercentage != null) {
                log.info("Recuperando porcentaje de cache: {}", cachedPercentage);
                return Mono.just(cachedPercentage);
            }
        }
        log.info("No se encontró ningun porcentaje en cache.");
        return Mono.error(new GetPercentageException("No se pudo obtener el porcentaje desde cache"));
    }
}
