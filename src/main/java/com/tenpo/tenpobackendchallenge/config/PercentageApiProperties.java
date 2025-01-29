package com.tenpo.tenpobackendchallenge.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "percentage-api")
@Getter @Setter
public class PercentageApiProperties {
    private String url;
    private String uri;
}
