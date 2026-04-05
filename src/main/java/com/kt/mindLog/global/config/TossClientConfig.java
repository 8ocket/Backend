package com.kt.mindLog.global.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.kt.mindLog.global.property.TossProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class TossClientConfig {
	private final TossProperties tossProperties;

	@Bean
	public WebClient tossWebClient() {
		return WebClient.builder()
			.baseUrl(tossProperties.getBaseUrl())
			.defaultHeader("Authorization", "Basic " + encode(tossProperties.getSecretKey()))
			.defaultHeader("Content-Type", "application/json")
			.build();
	}

	private String encode(String secretKey) {
		return Base64.getEncoder()
			.encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
	}
}
