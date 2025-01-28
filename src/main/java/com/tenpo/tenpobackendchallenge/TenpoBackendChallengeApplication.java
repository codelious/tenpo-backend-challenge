package com.tenpo.tenpobackendchallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TenpoBackendChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.run(TenpoBackendChallengeApplication.class, args);
	}

}
