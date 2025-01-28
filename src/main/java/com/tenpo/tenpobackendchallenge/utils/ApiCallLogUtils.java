package com.tenpo.tenpobackendchallenge.utils;

import com.tenpo.tenpobackendchallenge.dto.ApiCallLogDto;
import com.tenpo.tenpobackendchallenge.model.ApiCallLog;

public class ApiCallLogUtils {
    public static ApiCallLog toApiCallLog(ApiCallLogDto apiCallLogDto) {
        ApiCallLog apiCallLog = new ApiCallLog();
        apiCallLog.setEndpoint(apiCallLogDto.getEndpoint());
        apiCallLog.setResponse(apiCallLogDto.getResponse());
        apiCallLog.setParameters(apiCallLogDto.getParameters());
        apiCallLog.setTimestamp(apiCallLogDto.getTimestamp());
        apiCallLog.setHttpStatus(apiCallLogDto.getHttpStatus());
        return apiCallLog;
    }

    public static ApiCallLogDto toDto(ApiCallLog apiCallLog) {
        ApiCallLogDto apiCallLogDto = new ApiCallLogDto();
        apiCallLogDto.setEndpoint(apiCallLog.getEndpoint());
        apiCallLogDto.setTimestamp(apiCallLog.getTimestamp());
        apiCallLogDto.setResponse(apiCallLog.getResponse());
        apiCallLogDto.setParameters(apiCallLog.getParameters());
        apiCallLogDto.setHttpStatus(apiCallLog.getHttpStatus());
        return apiCallLogDto;
    }
}
