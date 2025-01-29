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
}
