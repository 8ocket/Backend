package com.kt.mindLog.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.kt.mindLog.global.property.StreamProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

	private final StreamProperties streamProperties;

	@Bean
	public WebClient webClient() {
		return WebClient.builder()
			.baseUrl(streamProperties.getBaseurl())
			.build();
	}
}
