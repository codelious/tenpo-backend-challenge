package com.tenpo.tenpobackendchallenge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiCallLogDto {
    private LocalDateTime timestamp;
    private String endpoint;
    private String parameters;
    private String response;
    private int httpStatus;
}
