package com.tenpo.tenpobackendchallenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("api_call_log")
public class ApiCallLog {

    @Id
    private Long id;

    @Column("timestamp")
    private LocalDateTime timestamp;

    @Column("endpoint")
    private String endpoint;

    @Column("parameters")
    private String parameters;

    @Column("response")
    private String response;

    @Column("http_status")
    private int httpStatus;

}
