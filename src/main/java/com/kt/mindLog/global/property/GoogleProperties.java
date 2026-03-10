package com.kt.mindLog.global.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@ConfigurationProperties(prefix = "google")
@AllArgsConstructor
@Getter
public class GoogleProperties {
	private final String clientId;
	private final String clientSecret;
	private final String redirectUri;
}
