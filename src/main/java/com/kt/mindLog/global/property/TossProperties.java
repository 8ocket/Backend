package com.kt.mindLog.global.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;

@ConfigurationProperties(prefix = "toss")
@Getter
public class TossProperties {
	private String baseUrl;
	private String clientKey;
	private String secretKey;
}
